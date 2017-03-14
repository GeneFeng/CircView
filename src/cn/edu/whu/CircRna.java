package cn.edu.whu;

import java.io.Serializable;
import java.util.TreeMap;

/**
 * CircRna
 * 
 * @author Gene
 *
 */
public class CircRna implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * circRNA ID
	 */
	private String circRnaID;

	/**
	 * Chromosome name
	 */
	private String chrom;

	/**
	 * circRNA start point;
	 */
	private long startPoint;

	/**
	 * circRNA end point;
	 */
	private long endPoint;

	/**
	 * strand
	 */
	private String strand;

	/**
	 * junction reads
	 */
	private int junctionReads;

	/**
	 * "geneName transcriptName" "geneName transcriptName" pair
	 */
	private TreeMap<String, String> geneTranscrpits;

	/**
	 * Repeat Times
	 */
	// private int repeat;

	private TreeMap<String, Integer> samples;
	private TreeMap<String, Integer> circTools;
	private TreeMap<String, Integer> files;

	/**
	 * exon or intron or intergenic
	 */
	private String region;

	/**
	 * mRNA or lncRNA or unknown
	 */
	private String circRnaType;

	/**
	 * Constructor of CircRna
	 * 
	 * @param geneName
	 */
	public CircRna(String circRnaID) {
		this.circRnaID = circRnaID;
		geneTranscrpits = new TreeMap<String, String>();
		samples = new TreeMap<String, Integer>();
		circTools = new TreeMap<String, Integer>();
		files = new TreeMap<String, Integer>();
	}

	public String getCircRnaID() {
		return circRnaID;
	}

	public void setCircRnaID(String circRnaID) {
		this.circRnaID = circRnaID;
	}

	public String getChrom() {
		return chrom;
	}

	public void setChrom(String chrom) {
		this.chrom = chrom;
	}

	public long getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(long startPoint) {
		this.startPoint = startPoint;
	}

	public long getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(long endPoint) {
		this.endPoint = endPoint;
	}

	public String getStrand() {
		return strand;
	}

	public void setStrand(String strand) {
		this.strand = strand;
	}

	public int getJunctionReads() {
		return junctionReads;
	}

	public void setJunctionReads(int junctionReads) {
		this.junctionReads = junctionReads;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getCircRnaType() {
		return circRnaType;
	}

	public void setCircRnaType(String circRnaType) {
		this.circRnaType = circRnaType;
	}

	/**
	 * @return the geneTranscrpits
	 */
	public TreeMap<String, String> getGeneTranscrpits() {
		return geneTranscrpits;
	}

	/**
	 * @param geneTranscrpits
	 *            the geneTranscrpits to set
	 */
	public void setGeneTranscrpits(TreeMap<String, String> geneTranscrpits) {
		this.geneTranscrpits = geneTranscrpits;
	}

	/**
	 * @return the samples
	 */
	public TreeMap<String, Integer> getSamples() {
		return samples;
	}

	/**
	 * @param samples
	 *            the samples to set
	 */
	public void setSamples(TreeMap<String, Integer> samples) {
		this.samples = samples;
	}

	/**
	 * @return the circTools
	 */
	public TreeMap<String, Integer> getCircTools() {
		return circTools;
	}

	/**
	 * @param circTools
	 *            the circTools to set
	 */
	public void setCircTools(TreeMap<String, Integer> circTools) {
		this.circTools = circTools;
	}

	/**
	 * @return the files
	 */
	public TreeMap<String, Integer> getFiles() {
		return files;
	}

	/**
	 * @param files
	 *            the files to set
	 */
	public void setFiles(TreeMap<String, Integer> files) {
		this.files = files;
	}

	// /**
	// * @return the repeat
	// */
	// public int getRepeat() {
	// return repeat;
	// }
	//
	// /**
	// * @param repeat the repeat to set
	// */
	// public void setRepeat(int repeat) {
	// this.repeat = repeat;
	// }
}
