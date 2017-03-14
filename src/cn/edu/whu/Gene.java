package cn.edu.whu;

import java.io.Serializable;
import java.util.TreeMap;
import java.util.Vector;

/**
 * Gene class
 * 
 * @author Gene
 *
 */
public class Gene implements Serializable{
	
	private static final long serialVersionUID = 1L;
	/**
	 * gene's name
	 */
	private String geneName;
	/**
	 * gene's transcripts
	 */
	private TreeMap<String, GeneTranscript> geneTranscripts;
	/**
	 * gene's circRNA samples
	 */
	
	private Vector<Exon> allExons;
	/**
	 * gene's reference
	 */
	private GeneTranscript geneReference;

	/**
	 * Constructor of Gene
	 * 
	 * @param geneName
	 */
	public Gene(String geneName) {
		this.setGeneName(geneName);
		geneTranscripts = new TreeMap<String, GeneTranscript>();
		allExons = new Vector<Exon>();
		geneReference = new GeneTranscript(geneName);
	}

	/**
	 * get Gene's name
	 * 
	 * @return geneName
	 */
	public String getGeneName() {
		return geneName;
	}

	/**
	 * set Gene's name
	 * 
	 * @param geneName
	 */
	public void setGeneName(String geneName) {
		this.geneName = geneName;
	}

	/**
	 * get gene's transcripts
	 * 
	 * @return geneTranscripts
	 */
	public TreeMap<String, GeneTranscript> getGeneTranscripts() {
		return geneTranscripts;
	}

	/**
	 * set gene's transcripts
	 * 
	 * @param geneTranscripts
	 */
	public void setGeneTranscripts(TreeMap<String, GeneTranscript> geneTranscripts) {
		this.geneTranscripts = geneTranscripts;
	}

	/**
	 * get all exons
	 * 
	 * @return allExons
	 */
	public Vector<Exon> getAllExons() {
		return allExons;
	}

	/**
	 * set all exons
	 * 
	 * @param allExons
	 */
	public void setAllExons(Vector<Exon> allExons) {
		this.allExons = allExons;
	}

	/**
	 * get gene's reference
	 * 
	 * @return geneReference
	 */
	public GeneTranscript getGeneReference() {
		return geneReference;
	}

	/**
	 * set gene's reference
	 * 
	 * @param geneReference
	 */
	public void setGeneReference(GeneTranscript geneReference) {
		this.geneReference = geneReference;
	}

}
