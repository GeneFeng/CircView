package cn.edu.whu;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

import cn.edu.whu.exception.FileReadException;
import cn.edu.whu.ui.DataLoadingDialog;
import cn.edu.whu.util.Constant;

public class CircRnaTool_backup implements Serializable {
	private static final long serialVersionUID = 1L;

	private TreeMap<String, Gene> genes;
	// circRnaID to CircRna pair
	private TreeMap<String, CircRna> circRnas;
	private TreeMap<String, Integer> circRnasNum;
	private int circRnaSampleNum;
	// txStartPoint to geneNamesMap
	private TreeMap<Long, TreeMap<String, String>> geneTxStart2Name;
	private TreeMap<Long, TreeMap<String, String>> geneTxEnd2Name;
	private TreeMap<String, ArrayList<Long>> chr2TxStartList;
	private TreeMap<String, ArrayList<Long>> chr2TxEndList;

	private String speciesName;
	private String circRnaTool;

	public CircRnaTool_backup() {
		genes = new TreeMap<String, Gene>();
		circRnas = new TreeMap<String, CircRna>();
		circRnasNum = new TreeMap<String, Integer>();
		circRnaSampleNum = 0;
		geneTxStart2Name = new TreeMap<Long, TreeMap<String, String>>();
		geneTxEnd2Name = new TreeMap<Long, TreeMap<String, String>>();
		chr2TxStartList = new TreeMap<String, ArrayList<Long>>();
		chr2TxEndList = new TreeMap<String, ArrayList<Long>>();
		this.speciesName = "";
		this.circRnaTool = "";
	}

	public boolean initSpeciesDataFromFile(File file) {
		return initGeneTranscript(file);
	}

	public boolean initCircRnaDataFromFiles(String circRnaTool, File[] files) throws FileReadException {
		boolean ret = true;
		if (this.circRnaTool.equals("") || this.circRnaTool.equals(circRnaTool)) {
			addCircRnaData(circRnaTool, files);
			this.circRnaTool = circRnaTool;
		} else {
			CircView.log.info(circRnaTool + " is not suit for " + this.circRnaTool);
			ret = false;
		}
		return ret;
	}

	private void addCircRnaData(String toolName, File[] files) throws FileReadException {
		if (toolName.equalsIgnoreCase(Constant.TOOL_CIRCRNAFINDER)
				|| toolName.equalsIgnoreCase(Constant.TOOL_CIRCEXPLORER)
				|| toolName.equalsIgnoreCase(Constant.TOOL_FIND_CIRC)
				|| toolName.equalsIgnoreCase(Constant.TOOL_MAPSPLICE)) {
			for (File file : files) {
				if (!parseCircRnaFinderFile(file)) {
					throw new FileReadException("Can not open [" + file.getName() + "] or FORMAT ERROR!");
				}
			}
		} else if (toolName.equalsIgnoreCase(Constant.TOOL_CIRI)) {
			for (File file : files) {
				if (!parseCiriFile(file)) {
					throw new FileReadException("Can not open [" + file.getName() + "] or FORMAT ERROR!");
				}
			}
		} else {
			for (File file : files) {
				if (!parseCircRnaFinderFile(file)) {
					throw new FileReadException("Can not open [" + file.getName() + "] or FORMAT ERROR!");
				}
			}
		}
		// Calculate CircRNA's Recurrent Value
		this.setCircRnaSampleNum(getCircRnaSampleNum() + files.length);
		assignAll(this.getGenes(), this.getCircRnas(), this.getChr2TxStartList(), this.getChr2TxEndList(),
				this.getGeneTxStart2Name(), this.getGeneTxEnd2Name());
	}

	private boolean initGeneTranscript(File file) {
		boolean ret = parseGeneTranscriptFile(file);
		if (ret) {
			preAssign(this.getGenes(), this.getChr2TxStartList(), this.getChr2TxEndList(), this.getGeneTxStart2Name(),
					this.getGeneTxEnd2Name());
		}
		return ret;
	}

	private boolean parseGeneTranscriptFile(File file) {
		boolean ret = true;
		CircView.log.info("Parsing " + file.getName());
		BufferedReader reader = null;
		try {
			if (file.isFile() && file.exists()) {
				reader = new BufferedReader(new FileReader(file));
				String lineTxt = null;
				while ((lineTxt = reader.readLine()) != null) {
					if (lineTxt.startsWith("GeneName")) {
						continue;
					}
					String[] parts = lineTxt.split("\t");
					// parts[0] Gene Name
					// parts[1] Gene Transcript Name
					// parts[2] Chromosome name
					// parts[3] + or - for strand
					// parts[4] Transcription start position
					// parts[5] Transcription end position
					// parts[6] Coding region start
					// parts[7] Coding region end
					// parts[8] Number of exons
					// parts[9] Exon start postions
					// parts[10] Exon end postions
					String geneName = parts[0];
					GeneTranscript geneTranscript = new GeneTranscript(geneName);
					geneTranscript.setTranscriptName(parts[1]);
					geneTranscript.setChrom(parts[2]);
					geneTranscript.setStrand(parts[3]);
					geneTranscript.setTxStart(Long.parseLong(parts[4]));
					geneTranscript.setTxEnd(Long.parseLong(parts[5]));
					geneTranscript.setCdsStart(Long.parseLong(parts[6]));
					geneTranscript.setCdsEnd(Long.parseLong(parts[7]));
					geneTranscript.setExonCount(Integer.parseInt(parts[8]));
					String[] exonStarts = parts[9].split(",");
					for (int i = 0; i < exonStarts.length; i++) {
						geneTranscript.getExonStarts().addElement(Long.parseLong(exonStarts[i]));
					}
					String[] exonEnds = parts[10].split(",");
					for (int i = 0; i < exonEnds.length; i++) {
						geneTranscript.getExonEnds().addElement(Long.parseLong(exonEnds[i]));
					}
					if (genes.containsKey(geneName.toUpperCase())) {
						genes.get(geneName.toUpperCase()).getGeneTranscripts().put(geneTranscript.getTranscriptName(),
								geneTranscript);
					} else {
						Gene gene = new Gene(geneName);
						gene.getGeneTranscripts().put(geneTranscript.getTranscriptName(), geneTranscript);
						genes.put(geneName.toUpperCase(), gene);
					}
				}
				reader.close();
			} else {
				ret = false;
				CircView.log.info("Can't open the file: " + file.getName());
			}
		} catch (Exception e) {
			ret = false;
			CircView.log.warn(e.getMessage());
		}
		return ret;
	}

	private boolean parseCircRnaFinderFile(File file) {
		boolean ret = true;
		CircView.log.info("Parsing " + file.getName());
		BufferedReader reader = null;
		try {
			if (file.isFile() && file.exists()) {
				reader = new BufferedReader(new FileReader(file));
				String lineTxt = null;
				while ((lineTxt = reader.readLine()) != null) {
					String[] parts = lineTxt.split("\t");
					// parts[0] Chromosome name
					// parts[1] start position
					// parts[2] end position
					// parts[3]
					// parts[4]
					// parts[5] + or - for strand
					String circRnaId = parts[0] + ":" + parts[1] + "|" + parts[2];
					CircRna circRna = new CircRna(circRnaId);
					circRna.setChrom(parts[0]);
					circRna.setStartPoint(Long.parseLong(parts[1]));
					circRna.setEndPoint(Long.parseLong(parts[2]));
					circRna.setStrand(parts[5]);
					if (circRnas.containsKey(circRnaId.toUpperCase())) {
						int n = circRnasNum.get(circRnaId.toUpperCase());
						circRnasNum.put(circRnaId.toUpperCase(), n + 1);
					} else {
						circRnas.put(circRnaId.toUpperCase(), circRna);
						circRnasNum.put(circRnaId.toUpperCase(), 1);
					}
				}
				reader.close();
			} else {
				CircView.log.warn("Can't find the file: " + file.getName());
			}
		} catch (Exception e) {
			CircView.log.warn(e.getMessage());
			ret = false;
		}
		return ret;
	}

	private boolean parseCiriFile(File file) {
		boolean ret = true;
		CircView.log.info("Parsing " + file.getName());
		BufferedReader reader = null;
		try {
			if (file.isFile() && file.exists()) {
				reader = new BufferedReader(new FileReader(file));
				String lineTxt = null;
				while ((lineTxt = reader.readLine()) != null) {
					String[] parts = lineTxt.split("\t");
					// parts[0] CircRNA ID
					// parts[1] Chromosome name
					// parts[2] start position
					// parts[3] end position
					// parts[10] + or - for strand
					String circRnaId = parts[0];
					CircRna circRna = new CircRna(circRnaId);
					circRna.setChrom(parts[1]);
					circRna.setStartPoint(Long.parseLong(parts[2]));
					circRna.setEndPoint(Long.parseLong(parts[3]));
					circRna.setStrand(parts[10]);
					if (circRnas.containsKey(circRnaId.toUpperCase())) {
						int n = circRnasNum.get(circRnaId.toUpperCase());
						circRnasNum.put(circRnaId.toUpperCase(), n + 1);
					} else {
						circRnas.put(circRnaId.toUpperCase(), circRna);
						circRnasNum.put(circRnaId.toUpperCase(), 1);
					}
				}
				reader.close();
			} else {
				CircView.log.warn("Can't find the file: " + file.getName());
				ret = false;
			}
		} catch (Exception e) {
			CircView.log.warn(e.getMessage());
			ret = false;
		}
		return ret;
	}

	private void preAssign(TreeMap<String, Gene> allGene, TreeMap<String, ArrayList<Long>> txStartMap,
			TreeMap<String, ArrayList<Long>> txEndMap, TreeMap<Long, TreeMap<String, String>> txStart2Name,
			TreeMap<Long, TreeMap<String, String>> txEnd2Name) {
		// Create Chrom -- > txStart-geneNames pair
		for (String geneName : allGene.keySet()) {
			String chrom = "";
			TreeMap<Long, Integer> tmpUniqueStart = new TreeMap<Long, Integer>();
			TreeMap<Long, Integer> tmpUniqueEnd = new TreeMap<Long, Integer>();
			TreeMap<String, GeneTranscript> transcripts = allGene.get(geneName.toUpperCase()).getGeneTranscripts();
			for (String transcriptName : transcripts.keySet()) {
				GeneTranscript transcript = transcripts.get(transcriptName);
				chrom = transcript.getChrom();
				Long txStart = transcript.getTxStart();
				Long txEnd = transcript.getTxEnd();
				if (txStart2Name.get(txStart) != null) {
					TreeMap<String, String> genesTmp = txStart2Name.get(txStart);
					genesTmp.put(geneName, geneName);
					txStart2Name.put(txStart, genesTmp);
				} else {
					TreeMap<String, String> genesTmp = new TreeMap<String, String>();
					genesTmp.put(geneName, geneName);
					txStart2Name.put(txStart, genesTmp);
				}
				if (txEnd2Name.get(txEnd) != null) {
					TreeMap<String, String> genesTmp = txEnd2Name.get(txEnd);
					genesTmp.put(geneName, geneName);
					txEnd2Name.put(txEnd, genesTmp);
				} else {
					TreeMap<String, String> genesTmp = new TreeMap<String, String>();
					genesTmp.put(geneName, geneName);
					txEnd2Name.put(txEnd, genesTmp);
				}
				tmpUniqueStart.put(txStart, 1);
				tmpUniqueEnd.put(txEnd, 1);
			}
			// Create Unique txStart and txEnd list
			ArrayList<Long> uniqueStartList = new ArrayList<Long>();
			ArrayList<Long> uniqueEndList = new ArrayList<Long>();
			for (Long txStart : tmpUniqueStart.keySet()) {
				uniqueStartList.add(txStart);
			}
			for (Long txEnd : tmpUniqueEnd.keySet()) {
				uniqueEndList.add(txEnd);
			}
			// Create Chrom --> Unique List
			if (txStartMap.get(chrom) != null) {
				txStartMap.get(chrom).addAll(uniqueStartList);
			} else {
				txStartMap.put(chrom, uniqueStartList);
			}
			if (txEndMap.get(chrom) != null) {
				txEndMap.get(chrom).addAll(uniqueEndList);
			} else {
				txEndMap.put(chrom, uniqueEndList);
			}
		}

		// Sort Unique List
		for (String chr : txStartMap.keySet()) {
			Collections.sort(txStartMap.get(chr));
		}
		for (String chr : txEndMap.keySet()) {
			Collections.sort(txEndMap.get(chr));
		}
	}

	private void assign(CircRna circRna, TreeMap<String, Gene> allGene, TreeMap<String, ArrayList<Long>> chr2StartList,
			TreeMap<String, ArrayList<Long>> chr2EndList, TreeMap<Long, TreeMap<String, String>> txStart2Name,
			TreeMap<Long, TreeMap<String, String>> txEnd2Name) {
		String chrom = circRna.getChrom();
		Long startPoint = circRna.getStartPoint();
		Long endPoint = circRna.getEndPoint();
		ArrayList<Long> txStartList = chr2StartList.get(chrom);
		if (txStartList == null) {
			CircView.log.info("circRNA_ID [" + circRna.getCircRnaID() + "] can not find related chrom");
			return;
		}
		ArrayList<Long> txEndList = chr2EndList.get(chrom);
		if (txEndList == null) {
			CircView.log.info("circRNA_ID [" + circRna.getCircRnaID() + "] can not find related chrom");
			return;
		}
		// 折半查找快速定位txStart的index

		int indexByTxStart = splitHalfByTxStart(txStartList, startPoint.longValue(), 0, txStartList.size() - 1);
		// index的边界控制
		indexByTxStart = (indexByTxStart < 0) ? 0 : indexByTxStart;
		indexByTxStart = (indexByTxStart > txStartList.size() - 1) ? txStartList.size() - 1 : indexByTxStart;
		// 折半查找快速定位txEnd的index
		int indexByTxEnd = splitHalfByTxEnd(txEndList, endPoint.longValue(), 0, txEndList.size() - 1);
		// index的边界控制
		indexByTxEnd = (indexByTxEnd <= 1) ? 0 : indexByTxEnd;
		indexByTxEnd = (indexByTxEnd > txEndList.size() - 1) ? txEndList.size() - 1 : indexByTxEnd;

		TreeMap<String, Integer> geneNames = new TreeMap<String, Integer>();
		// 判断indexByTxStart和indexByTxEnd用哪个效率高
		if ((indexByTxStart + 1) < (txEndList.size() - indexByTxEnd)) {
			// System.out.println("Find in start: " + txStartList.size());
			// 选择indexByTxStart来进行分析
			for (int index = 0; index <= indexByTxStart; index++) {
				for (String name : txStart2Name.get(txStartList.get(index)).keySet()) {
					geneNames.put(name, 1);
				}
			}
		} else {
			// System.out.println("Find in end: " + txEndList.size());
			// 选择indexByTxEnd来进行分析
			for (int index = indexByTxEnd; index < txEndList.size(); index++) {
				for (String name : txEnd2Name.get(txEndList.get(index)).keySet()) {
					geneNames.put(name, 1);
				}
			}
		}

		// gene location, circRNA type, region
		TreeMap<String, Integer> geneLocation = new TreeMap<String, Integer>();
		TreeMap<String, Integer> region = new TreeMap<String, Integer>();
		// System.out.println("Index[" + indexByTxStart + "]\t" + "GeneName[" +
		// geneNames.toString() + "]");
		int exonSign = 0;
		for (String name : geneNames.keySet()) {
			int geneLocationSign = 0;
			int mRnaSign = 0;
			int lncRnaSign = 0;
			Gene gene = allGene.get(name.toUpperCase());
			TreeMap<String, GeneTranscript> transcripts;
			if (gene != null) {
				transcripts = gene.getGeneTranscripts();
			} else {
				return;
			}
			for (String transcriptName : transcripts.keySet()) {
				GeneTranscript trans = transcripts.get(transcriptName);
				if ((trans.getTxStart().longValue() <= startPoint.longValue())
						&& (endPoint.longValue() <= trans.getTxEnd().longValue())) {
					geneLocationSign++;
					geneLocation.put(name + " " + trans.getTranscriptName(), 1);

					if (trans.getCircRnas().get(circRna.getCircRnaID()) != null) {
						trans.getCircRnasNum().put(circRna.getCircRnaID(),
								circRnasNum.get(circRna.getCircRnaID().toUpperCase()));
					} else {
						trans.getCircRnas().put(circRna.getCircRnaID(), circRna);
						trans.getCircRnasNum().put(circRna.getCircRnaID(),
								circRnasNum.get(circRna.getCircRnaID().toUpperCase()));
					}
				}
				if (trans.getCdsStart().longValue() < trans.getCdsEnd().longValue()) {
					mRnaSign++;
				} else {
					lncRnaSign++;
				}
				for (int i = 0; i < trans.getExonCount(); i++) {
					Long exonStart = trans.getExonStarts().get(i);
					Long exonEnd = trans.getExonEnds().get(i);
					if (((exonStart.longValue() - Constant.ASSIGN_TOLERATION <= startPoint.longValue())
							&& (startPoint.longValue() < exonEnd.longValue() + Constant.ASSIGN_TOLERATION))
							|| ((exonStart.longValue() - Constant.ASSIGN_TOLERATION < endPoint.longValue())
									&& (endPoint.longValue() <= exonEnd.longValue() + Constant.ASSIGN_TOLERATION))
							|| ((startPoint.longValue() < exonStart.longValue() - Constant.ASSIGN_TOLERATION)
									&& (exonEnd.longValue() < endPoint.longValue() + Constant.ASSIGN_TOLERATION))) {
						exonSign++;
					}
				}
			}
			if (geneLocationSign > 0) {
				if (mRnaSign > 0) {
					region.put("mRNA", 1);
				} else {
					region.put("lncRNA", 1);
				}
			}
		}

		if (geneLocation.size() > 0) {
			for (String n : geneLocation.keySet()) {
				circRna.getGeneTranscrpits().put(n, n);
			}
			if (exonSign > 0) {
				circRna.setCircRnaType("exon");
			} else {
				circRna.setCircRnaType("intron");
			}
			if ((region.get("mRNA") != null) && (region.get("lncRNA") == null)) {
				circRna.setRegion("mRNA");
			} else if ((region.get("mRNA") == null) && (region.get("lncRNA") != null)) {
				circRna.setRegion("lncRNA");
			}
			if ((region.get("mRNA") != null) && (region.get("lncRNA") != null)) {
				circRna.setRegion("mRNA,lncRNA");
			}
		} else {
			circRna.getGeneTranscrpits().put("n/a", "n/a");
			circRna.setCircRnaType("intergenic");
			circRna.setRegion("Unknown");
		}
	}

	private void assignAll(TreeMap<String, Gene> allGene, TreeMap<String, CircRna> allCircRna,
			TreeMap<String, ArrayList<Long>> chr2StartList, TreeMap<String, ArrayList<Long>> chr2EndList,
			TreeMap<Long, TreeMap<String, String>> txStart2Name, TreeMap<Long, TreeMap<String, String>> txEnd2Name) {
		int num = 0;
		for (String circRnaId : allCircRna.keySet()) {
			if (0 == (++num % 1000)) {
				CircView.log.info("Mapping: [" + num + "|" + allCircRna.size() + "]");
			}
			CircRna circRna = allCircRna.get(circRnaId);
			assign(circRna, allGene, chr2StartList, chr2EndList, txStart2Name, txEnd2Name);
		}
	}

	private int splitHalfByTxStart(ArrayList<Long> arrayData, Long txStart, int start, int end) {
		int index = (start + end) / 2;
		Long data = arrayData.get(index);
		// System.out.print("array[" + index + "]=" + data + "(" + start + "," +
		// end + ")" + "\t");
		if (start > end) {
			// return -1;
			return end;
		}
		if (data.longValue() == txStart.longValue()) {
			return index;
		} else {
			if (data.longValue() < txStart.longValue()) {
				return splitHalfByTxStart(arrayData, txStart, index + 1, end);
			} else {
				return splitHalfByTxStart(arrayData, txStart, start, index - 1);
			}
		}
	}

	private int splitHalfByTxEnd(ArrayList<Long> arrayData, Long txEnd, int start, int end) {
		int index = (start + end) / 2;
		Long data = arrayData.get(index);
		// System.out.print("array[" + index + "]=" + data + "(" + start + "," +
		// end + ")" + "\t");
		if (start > end) {
			// return -1;
			return start;
		}
		if (data.longValue() == txEnd.longValue()) {
			return index;
		} else {
			if (data.longValue() < txEnd.longValue()) {
				return splitHalfByTxEnd(arrayData, txEnd, index + 1, end);
			} else {
				return splitHalfByTxEnd(arrayData, txEnd, start, index - 1);
			}
		}
	}

	public CircRnaTool_backup deepClone() {
		ByteArrayOutputStream byteOut = null;
		ObjectOutputStream objOut = null;
		ByteArrayInputStream byteIn = null;
		ObjectInputStream objIn = null;

		try {
			byteOut = new ByteArrayOutputStream();
			objOut = new ObjectOutputStream(byteOut);
			objOut.writeObject(this);

			byteIn = new ByteArrayInputStream(byteOut.toByteArray());
			objIn = new ObjectInputStream(byteIn);

			return (CircRnaTool_backup) objIn.readObject();
		} catch (IOException e) {
			throw new RuntimeException("Clone Object failed in IO.", e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Class not found.", e);
		} finally {
			try {
				byteIn = null;
				byteOut = null;
				if (objOut != null)
					objOut.close();
				if (objIn != null)
					objIn.close();
			} catch (IOException e) {
			}
		}
	}

	public TreeMap<String, Gene> getGenes() {
		return genes;
	}

	public void setGenes(TreeMap<String, Gene> genes) {
		this.genes = genes;
	}

	public TreeMap<String, CircRna> getCircRnas() {
		return circRnas;
	}

	public void setCircRnas(TreeMap<String, CircRna> circRnas) {
		this.circRnas = circRnas;
	}

	public TreeMap<String, Integer> getCircRnasNum() {
		return circRnasNum;
	}

	public void setCircRnasNum(TreeMap<String, Integer> circRnasNum) {
		this.circRnasNum = circRnasNum;
	}

	public int getCircRnaSampleNum() {
		return circRnaSampleNum;
	}

	public void setCircRnaSampleNum(int circRnaSampleNum) {
		this.circRnaSampleNum = circRnaSampleNum;
	}

	public TreeMap<Long, TreeMap<String, String>> getGeneTxStart2Name() {
		return geneTxStart2Name;
	}

	public void setGeneTxStart2Name(TreeMap<Long, TreeMap<String, String>> geneTxStart2Name) {
		this.geneTxStart2Name = geneTxStart2Name;
	}

	public TreeMap<Long, TreeMap<String, String>> getGeneTxEnd2Name() {
		return geneTxEnd2Name;
	}

	public void setGeneTxEnd2Name(TreeMap<Long, TreeMap<String, String>> geneTxEnd2Name) {
		this.geneTxEnd2Name = geneTxEnd2Name;
	}

	public TreeMap<String, ArrayList<Long>> getChr2TxStartList() {
		return chr2TxStartList;
	}

	public void setChr2TxStartList(TreeMap<String, ArrayList<Long>> chr2TxStartList) {
		this.chr2TxStartList = chr2TxStartList;
	}

	public TreeMap<String, ArrayList<Long>> getChr2TxEndList() {
		return chr2TxEndList;
	}

	public void setChr2TxEndList(TreeMap<String, ArrayList<Long>> chr2TxEndList) {
		this.chr2TxEndList = chr2TxEndList;
	}

	public String getSpeciesName() {
		return speciesName;
	}

	public void setSpeciesName(String speciesName) {
		this.speciesName = speciesName;
	}

	public String getCircRnaTool() {
		return circRnaTool;
	}

	public void setCircRnaTool(String circRnaTool) {
		this.circRnaTool = circRnaTool;
	}

}
