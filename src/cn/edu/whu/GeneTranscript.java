package cn.edu.whu;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.TreeMap;
import java.util.Vector;

/**
 * GeneTranscript
 * 
 * @author Gene deal refFlat format file
 */
public class GeneTranscript implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * Name of gene as it appears in Genome Browser
	 */
	private String geneName;
	/**
	 * Name of gene
	 */
	private String transcriptName;
	/**
	 * Chromosome name
	 */
	private String chrom;
	/**
	 * + or - for strand
	 */
	private String strand;
	/**
	 * Transcription start position
	 */
	private Long txStart;
	/**
	 * Transcription end position
	 */
	private Long txEnd;
	/**
	 * Coding region start
	 */
	private Long cdsStart;
	/**
	 * Coding region end
	 */
	private Long cdsEnd;
	/**
	 * Number of exons
	 */
	private Integer exonCount;
	/**
	 * Exon start postions
	 */
	private Vector<Long> exonStarts;
	/**
	 * Exon end positons
	 */
	private Vector<Long> exonEnds;

	private TreeMap<String, CircRna> circRnas;
	private TreeMap<String, Integer> circRnasNum;
	private int totalJunctionReads;

	/**
	 * Constructor of class GeneTranscript
	 * 
	 * @param geneName
	 */
	public GeneTranscript(String geneName) {
		this.setGeneName(geneName);
		exonStarts = new Vector<Long>();
		exonEnds = new Vector<Long>();
		setCircRnas(new TreeMap<String, CircRna>());
		setCircRnasNum(new TreeMap<String, Integer>());
		setTotalJunctionReads(0);
	}

	/**
	 * get gene name
	 * 
	 * @return geneName
	 */
	public String getGeneName() {
		return geneName;
	}

	/**
	 * set gene name
	 * 
	 * @param geneName
	 */
	public void setGeneName(String geneName) {
		this.geneName = geneName;
	}

	/**
	 * get transcript name
	 * 
	 * @return transcriptName
	 */
	public String getTranscriptName() {
		return transcriptName;
	}

	/**
	 * set transcript name
	 * 
	 * @param transcriptName
	 */
	public void setTranscriptName(String transcriptName) {
		this.transcriptName = transcriptName;
	}

	/**
	 * get chrom
	 * 
	 * @return chrom
	 */
	public String getChrom() {
		return chrom;
	}

	/**
	 * set chrom
	 * 
	 * @param chrom
	 */
	public void setChrom(String chrom) {
		this.chrom = chrom;
	}

	/**
	 * get strand
	 * 
	 * @return strand
	 */
	public String getStrand() {
		return strand;
	}

	/**
	 * set strand
	 * 
	 * @param strand
	 */
	public void setStrand(String strand) {
		this.strand = strand;
	}

	/**
	 * get Tx start
	 * 
	 * @return txStart
	 */
	public Long getTxStart() {
		return txStart;
	}

	/**
	 * set Tx start
	 * 
	 * @param txStart
	 */
	public void setTxStart(Long txStart) {
		this.txStart = txStart;
	}

	/**
	 * get Tx end
	 * 
	 * @return txEnd
	 */
	public Long getTxEnd() {
		return txEnd;
	}

	/**
	 * set Tx end
	 * 
	 * @param txEnd
	 */
	public void setTxEnd(Long txEnd) {
		this.txEnd = txEnd;
	}

	/**
	 * get CDS start
	 * 
	 * @return cdsStart
	 */
	public Long getCdsStart() {
		return cdsStart;
	}

	/**
	 * set CDS start
	 * 
	 * @param cdsStart
	 */
	public void setCdsStart(Long cdsStart) {
		this.cdsStart = cdsStart;
	}

	/**
	 * get CDS end
	 * 
	 * @return cdsEnd
	 */
	public Long getCdsEnd() {
		return cdsEnd;
	}

	/**
	 * set CDS end
	 * 
	 * @param cdsEnd
	 */
	public void setCdsEnd(Long cdsEnd) {
		this.cdsEnd = cdsEnd;
	}

	/**
	 * get exon count
	 * 
	 * @return exonCount
	 */
	public Integer getExonCount() {
		return exonCount;
	}

	/**
	 * set exon count
	 * 
	 * @param exonCount
	 */
	public void setExonCount(Integer exonCount) {
		this.exonCount = exonCount;
	}

	/**
	 * get exon starts
	 * 
	 * @return exonStarts
	 */
	public Vector<Long> getExonStarts() {
		return exonStarts;
	}

	/**
	 * set exon starts
	 * 
	 * @param exonStarts
	 */
	public void setExonStarts(Vector<Long> exonStarts) {
		this.exonStarts = exonStarts;
	}

	/**
	 * get exon ends
	 * 
	 * @return exonEnds
	 */
	public Vector<Long> getExonEnds() {
		return exonEnds;
	}

	/**
	 * set exon ends
	 * 
	 * @param exonEnds
	 */
	public void setExonEnds(Vector<Long> exonEnds) {
		this.exonEnds = exonEnds;
	}

	/**
	 * @return the circRnaNum
	 */
	public TreeMap<String, Integer> getCircRnasNum() {
		return circRnasNum;
	}

	/**
	 * @param circRnaNum
	 *            the circRnaNum to set
	 */
	public void setCircRnasNum(TreeMap<String, Integer> circRnasNum) {
		this.circRnasNum = circRnasNum;
	}
	
	/**
	 * @return the totalJunctionReads
	 */
	public int getTotalJunctionReads() {
		return totalJunctionReads;
	}

	/**
	 * @param totalJunctionReads the totalJunctionReads to set
	 */
	public void setTotalJunctionReads(int totalJunctionReads) {
		this.totalJunctionReads = totalJunctionReads;
	}

	/**
	 * @return the circRnas
	 */
	public TreeMap<String, CircRna> getCircRnas() {
		return circRnas;
	}

	/**
	 * @param circRnas
	 *            the circRnas to set
	 */
	public void setCircRnas(TreeMap<String, CircRna> circRnas) {
		this.circRnas = circRnas;
	}

	public GeneTranscript deepClone() {
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

			return (GeneTranscript) objIn.readObject();
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

}
