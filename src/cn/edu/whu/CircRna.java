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
	 * "geneName transcriptName" "geneName transcriptName" pair
	 */
	private TreeMap<String, String> geneTranscrpits;
	
	/**
	 * Repeat Times
	 */
	private int repeat;

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
	 * @return the repeat
	 */
	public int getRepeat() {
		return repeat;
	}

	/**
	 * @param repeat the repeat to set
	 */
	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}
}
