package cn.edu.whu.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cn.edu.whu.CircRna;
import cn.edu.whu.GeneTranscript;
import cn.edu.whu.CircView;
import cn.edu.whu.util.Constant;
import cn.edu.whu.util.DbUtil;

public class CircRnaImagePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private GeneTranscript gtBackup;
	private GeneTranscript gt;
	private int toolNum;
	private int sampleNum;
	private final Vector<Color> colorList;
	private int IMAGE_PANEL_WIDTH;
	private int IMAGE_PANEL_HEIGHT;
	private float zoomValue;

	// Default Value
	private int IMAGE_MARGIN_LEFT = 20;
	private int IMAGE_MARGIN_RIGHT = 20;
	private int STRAND_ARROW_LENGTH = 20;
	private int ANNOTATION_FONT = 12;
	private int EXON_NUM_FONT = 12;
	private int CIRCRNA_INFO_FONT = 12;
	private float MRE_STROKE = 0.5f;
	private float DEFAULT_STROKE = 0.5f;
	private float INDICATION_STROKE = 0.5f;

	private int TRANSCRIPT_ANNOTATION_TOP = 20;
	private int TRANSCRIPT_IMAGE_TOP = 30;
	private int TRANSCRIPT_EXON_HEIGHT = 20;
	private int TRANSCRIPT_INTRON_HEIGHT = 1;

	private Connection conn;
	private BufferedImage circRnaImage;
	private TreeMap<String, TreeMap<String, Vector<String>>> circRnasRbp;
	private TreeMap<String, TreeMap<String, Vector<String>>> circRnasMre;

	// for MouseLinstener
	private Vector<Double> circX;
	private Vector<Double> circY;
	private double circR;

	public CircRnaImagePanel() {
		gtBackup = null;
		gt = null;
		circRnasRbp = new TreeMap<String, TreeMap<String, Vector<String>>>();
		circRnasMre = new TreeMap<String, TreeMap<String, Vector<String>>>();
		toolNum = 1;
		sampleNum = 1;
		colorList = new Vector<Color>();
		initColor();
		this.setBackground(Color.WHITE);
		circX = new Vector<Double>();
		circY = new Vector<Double>();
		circR = 0;
	}

	public void createOneImage(int width, int height, Connection c) {
		IMAGE_PANEL_WIDTH = width - 20;
		IMAGE_PANEL_HEIGHT = height - 50;
		zoomValue = 1;
		conn = c;
		circRnaImage = new BufferedImage(IMAGE_PANEL_WIDTH, IMAGE_PANEL_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		this.setSize(new Dimension(IMAGE_PANEL_WIDTH, IMAGE_PANEL_HEIGHT));
	}

	private void initColor() {
		colorList.addElement(Color.ORANGE);
		colorList.addElement(Color.CYAN);
		colorList.addElement(Color.GRAY);
		colorList.addElement(Color.GREEN);
		colorList.addElement(Color.LIGHT_GRAY);
		colorList.addElement(Color.MAGENTA);
		colorList.addElement(Color.PINK);
		colorList.addElement(Color.RED);
		colorList.addElement(Color.YELLOW);
		// for (int r = 0; r < 255; r += 10) {
		// for (int b = 0; b < 255; b += 10) {
		// for (int g = 0; g < 255; g += 10) {
		// colorList.add(new Color(r, b, g));
		// }
		// }
		// }
	}

	public GeneTranscript getGt() {
		return gt;
	}

	public void setGt(GeneTranscript gt, int sampleNum, int toolNum) {
		gtBackup = gt;
		this.sampleNum = sampleNum;
		this.toolNum = toolNum;
		this.gt = gtBackup.deepClone();
	}

	public int getToolNum() {
		return toolNum;
	}

	public void setToolNum(int toolNum) {
		this.toolNum = toolNum;
	}

	public int getSampleNum() {
		return sampleNum;
	}

	public void setSampleNum(int sampleNum) {
		this.sampleNum = sampleNum;
	}

	public void zoomIn() {
		zoomValue *= 1.1;
		if (zoomValue > 5) {
			zoomValue = 5;
		}
		this.setSize(new Dimension((int) (IMAGE_PANEL_WIDTH * zoomValue), (int) (IMAGE_PANEL_HEIGHT * zoomValue)));
		circRnaImage = new BufferedImage((int) (IMAGE_PANEL_WIDTH * zoomValue), (int) (IMAGE_PANEL_HEIGHT * zoomValue),
				BufferedImage.TYPE_INT_ARGB);
		this.repaint();
		/***********************************************************
		 * IMPORTANT: Override this method to make ScollPane valid *
		 ***********************************************************/
		this.revalidate();
	}

	public void zoomOut() {
		zoomValue *= 0.9;
		if (zoomValue < 1) {
			zoomValue = 1;
		}
		this.setSize(new Dimension((int) (IMAGE_PANEL_WIDTH * zoomValue), (int) (IMAGE_PANEL_HEIGHT * zoomValue)));
		circRnaImage = new BufferedImage((int) (IMAGE_PANEL_WIDTH * zoomValue), (int) (IMAGE_PANEL_HEIGHT * zoomValue),
				BufferedImage.TYPE_INT_ARGB);
		this.repaint();
		/***********************************************************
		 * IMPORTANT: Override this method to make ScollPane valid *
		 ***********************************************************/
		this.revalidate();
	}

	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// initConstantValue();
		if (null != this.getGt()) {
			this.drawCircRnaImage(circRnaImage, gt);
			g2d.drawImage(circRnaImage, 0, 0, this);
		}
	}

	private void initConstantValue(BufferedImage circRnaImage) {
		int baseWidth = circRnaImage.getWidth() / 50;
		int baseHeight = circRnaImage.getHeight() / 50;
		IMAGE_MARGIN_LEFT = baseWidth < 20 ? 20 : baseWidth;
		IMAGE_MARGIN_RIGHT = IMAGE_MARGIN_LEFT;
		TRANSCRIPT_ANNOTATION_TOP = (int) (IMAGE_MARGIN_LEFT * 1.5);
		STRAND_ARROW_LENGTH = IMAGE_MARGIN_LEFT;

		TRANSCRIPT_IMAGE_TOP = (int) (TRANSCRIPT_ANNOTATION_TOP * 1.5);
		TRANSCRIPT_INTRON_HEIGHT = (int) Math.round(TRANSCRIPT_ANNOTATION_TOP / 40.0);
		TRANSCRIPT_EXON_HEIGHT = TRANSCRIPT_INTRON_HEIGHT * 20;
		INDICATION_STROKE = (float) (TRANSCRIPT_INTRON_HEIGHT / 2 < 0.5f ? 0.5f : TRANSCRIPT_INTRON_HEIGHT / 2.0);
		ANNOTATION_FONT = (int) Math.round(TRANSCRIPT_EXON_HEIGHT / 1.2);
		EXON_NUM_FONT = (int) Math.round(ANNOTATION_FONT / 1.5);
		CIRCRNA_INFO_FONT = (int) Math.round(ANNOTATION_FONT / 1.8);
	}

	@Override
	/***********************************************************
	 * IMPORTANT: Override this method to make ScollPane valid *
	 ***********************************************************/
	public Dimension getPreferredSize() {
		// TODO Auto-generated method stub
		int width = 0;
		int height = 0;
		if (this != null) {
			width = this.getWidth();
			height = this.getHeight();
		}
		return new Dimension(width, height);
	}

	private void drawCircRnaImage(BufferedImage circRnaImage, GeneTranscript gt) {
		initConstantValue(circRnaImage);

		Graphics2D g2d = (Graphics2D) circRnaImage.getGraphics();

		g2d.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2d.setBackground(Color.WHITE);
		drawTranscript(g2d, circRnaImage, gt);
		drawCircRna(g2d, circRnaImage, gt);
	}

	private void drawTranscript(Graphics2D g2d, BufferedImage circRnaImage, GeneTranscript gt) {
		drawAnotation(g2d, circRnaImage, gt);
		drawIntrons(g2d, circRnaImage, gt);
		drawStrand(g2d, circRnaImage, gt);
		drawExons(g2d, circRnaImage, gt);
	}

	private void drawAnotation(Graphics2D g2d, BufferedImage circRnaImage, GeneTranscript gt) {
		// Draw Notation
		String notation = gt.getGeneName() + " [" + gt.getTranscriptName() + "]\t" + gt.getChrom() + "\t"
				+ "Exon Number[" + gt.getExonCount() + "]\tTx[" + gt.getTxStart() + "-" + gt.getTxEnd() + "]\t" + "Cds["
				+ gt.getCdsStart() + "-" + gt.getCdsEnd() + "]";
		g2d.setColor(Color.BLACK);
		g2d.setFont(new Font("TimesRoman", Font.PLAIN, ANNOTATION_FONT));
		g2d.setStroke(new BasicStroke(DEFAULT_STROKE, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
		g2d.drawString(notation, IMAGE_MARGIN_LEFT, TRANSCRIPT_ANNOTATION_TOP);
	}

	private void drawIntrons(Graphics2D g2d, BufferedImage circRnaImage, GeneTranscript gt) {
		// Draw Intron rects
		double intronXPosition = IMAGE_MARGIN_LEFT;
		double intronYPosition = TRANSCRIPT_IMAGE_TOP + TRANSCRIPT_EXON_HEIGHT / 2.0;
		double drawLength = circRnaImage.getWidth() - IMAGE_MARGIN_LEFT - IMAGE_MARGIN_RIGHT;
		Rectangle2D intron = new Rectangle2D.Double(intronXPosition, intronYPosition, drawLength,
				TRANSCRIPT_INTRON_HEIGHT);
		g2d.setColor(Color.BLACK);
		g2d.fill(intron);
	}

	private void drawStrand(Graphics2D g2d, BufferedImage circRnaImage, GeneTranscript gt) {
		Polygon arrow = new Polygon();
		if (gt.getStrand().equals("+")) {
			// draw arrow at right
			int xPosition = circRnaImage.getWidth() - IMAGE_MARGIN_RIGHT;
			int yPosition = TRANSCRIPT_IMAGE_TOP + TRANSCRIPT_EXON_HEIGHT / 2;
			arrow.addPoint(xPosition, yPosition);
			arrow.addPoint(xPosition - STRAND_ARROW_LENGTH / 2, yPosition - STRAND_ARROW_LENGTH / 4);
			arrow.addPoint(xPosition - STRAND_ARROW_LENGTH / 2, yPosition + STRAND_ARROW_LENGTH / 4);
		} else {
			// draw arrow at left
			int xPosition = IMAGE_MARGIN_LEFT;
			int yPosition = TRANSCRIPT_IMAGE_TOP + TRANSCRIPT_EXON_HEIGHT / 2;
			arrow.addPoint(xPosition, yPosition);
			arrow.addPoint(xPosition + STRAND_ARROW_LENGTH / 2, yPosition - STRAND_ARROW_LENGTH / 4);
			arrow.addPoint(xPosition + STRAND_ARROW_LENGTH / 2, yPosition + STRAND_ARROW_LENGTH / 4);
		}
		g2d.setColor(Color.BLACK);
		g2d.fill(arrow);
	}

	private void drawExons(Graphics2D g2d, BufferedImage circRnaImage, GeneTranscript gt) {
		long basePoint = gt.getTxStart();
		double lengthRatio = 1.0
				* (circRnaImage.getWidth() - IMAGE_MARGIN_LEFT - IMAGE_MARGIN_RIGHT - STRAND_ARROW_LENGTH)
				/ (gt.getTxEnd() - gt.getTxStart());
		double exonXPosition = 0.0;
		double exonYPosition = TRANSCRIPT_IMAGE_TOP;

		// Left for Strand Arrow
		if (gt.getStrand().equals("+")) {
			exonXPosition = IMAGE_MARGIN_LEFT;
		} else {
			exonXPosition = IMAGE_MARGIN_LEFT + STRAND_ARROW_LENGTH;
		}

		Vector<Rectangle2D> exonRects = new Vector<Rectangle2D>();
		for (int i = 0; i < gt.getExonCount(); i++) {
			double startPoint = (gt.getExonStarts().get(i) - basePoint) * lengthRatio;
			double endPoint = (gt.getExonEnds().get(i) - basePoint) * lengthRatio;
			double length = (endPoint - startPoint) < 1 ? 1 : (endPoint - startPoint);
			Rectangle2D exonRect = new Rectangle2D.Double(exonXPosition + startPoint, exonYPosition, length,
					TRANSCRIPT_EXON_HEIGHT);
			exonRects.addElement(exonRect);
			// Draw Exon No.
			int no = 0;
			if (gt.getStrand().equals("+")) {
				no = i + 1;
			} else {
				no = gt.getExonCount() - i;
			}
			g2d.setColor(Color.BLACK);
			g2d.setFont(new Font("TimesRoman", Font.PLAIN, EXON_NUM_FONT));
			g2d.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
			if (no % 5 == 0) {
				g2d.drawString(no + "", (int) (exonXPosition + startPoint),
						(int) (exonYPosition + TRANSCRIPT_EXON_HEIGHT + g2d.getFontMetrics().getHeight()));
			}
		}
		for (int i = 0; i < exonRects.size(); i++) {
			g2d.setColor(colorList.get(i % colorList.size()));
			g2d.fill(exonRects.get(i));
		}

	}

	private void drawCircRna(Graphics2D g2d, BufferedImage circRnaImage, GeneTranscript gt) {
		// Begin to draw images
		double imageWidth = circRnaImage.getWidth() - IMAGE_MARGIN_LEFT - IMAGE_MARGIN_RIGHT;
		double circImageWidth = imageWidth / gt.getCircRnas().size();
		// Circle Center Points for CircRNAs
		Vector<Double> centX = new Vector<Double>();
		Vector<Double> centY = new Vector<Double>();
		long firstCentX = Math.round(IMAGE_MARGIN_LEFT + circImageWidth / 2);
		for (int i = 0; i < gt.getCircRnas().size(); i++) {
			centX.add(firstCentX + i * circImageWidth);
			centY.add(circRnaImage.getHeight() / 2.0);
		}
		// Circle Diameter
		double circImageHeight = circRnaImage.getHeight() / 3.0;
		double circRectLength = (circImageHeight < circImageWidth) ? circImageHeight : circImageWidth;
		double diameter = 8 * circRectLength / 10;
		double lineStroke = circRectLength / 10;
		// for mouselistener
		setCircX(centX);
		setCircY(centY);
		setCircR(diameter / 2);
		// for each CircRNA
		int circNum = -1;
		for (String circRnaId : gt.getCircRnas().keySet()) {
			circNum++;
			CircRna circRna = gt.getCircRnas().get(circRnaId);
			long circStart = circRna.getStartPoint();
			long circEnd = circRna.getEndPoint();
			long circRnaLen = 0;
			// Ready for draw CircRNA
			TreeMap<Integer, String> exonsInfo = new TreeMap<Integer, String>();
			Vector<Integer> exonsId = new Vector<Integer>();
			for (int i = 0; i < gt.getExonCount(); i++) {
				long exonStart = gt.getExonStarts().get(i);
				long exonEnd = gt.getExonEnds().get(i);
				if ((circStart <= exonStart) && (exonEnd <= circEnd)) {
					circRnaLen += (exonEnd - exonStart);
					exonsInfo.put(i, exonStart + "-" + exonEnd);
					exonsId.add(i);
				} else if ((exonStart < circStart) && (circStart < exonEnd) && (exonEnd <= circEnd)) {
					circRnaLen += (exonEnd - circStart);
					exonsInfo.put(i, circStart + "-" + exonEnd);
					exonsId.add(i);
				} else if ((circStart <= exonStart) && (exonStart < circEnd) && (circEnd < exonEnd)) {
					circRnaLen += (circEnd - exonStart);
					exonsInfo.put(i, exonStart + "-" + circEnd);
					exonsId.add(i);
				} else if ((exonStart < circStart) && (circEnd < exonEnd)) {
					circRnaLen += (circEnd - circStart);
					exonsInfo.put(i, circStart + "-" + circEnd);
					exonsId.add(i);
				}
			}
			// Calulate Arc of Circle
			double arcRatio = 360.0 / circRnaLen;
			TreeMap<Integer, Integer> exonArcsDegree = new TreeMap<Integer, Integer>();
			for (int exonId : exonsId) {
				String[] str = exonsInfo.get(exonId).split("-");
				long start = Long.parseLong(str[0]);
				long end = Long.parseLong(str[1]);
				double degree = arcRatio * (end - start);
				int exonArcDegree = (int) Math.round(degree) < 1 ? 1 : (int) Math.round(degree);
				exonArcsDegree.put(exonId, exonArcDegree);
			}

			// Draw the indication line
			long basePoint = gt.getTxStart();
			double lengthRatio = 1.0
					* (circRnaImage.getWidth() - IMAGE_MARGIN_LEFT - IMAGE_MARGIN_RIGHT - STRAND_ARROW_LENGTH)
					/ (gt.getTxEnd() - gt.getTxStart());
			// Start Exon Line
			int x = IMAGE_MARGIN_LEFT;
			if (gt.getStrand().equals("-")) {
				x += STRAND_ARROW_LENGTH;
			}
			x += (int) Math.round((circRna.getStartPoint() - basePoint) * lengthRatio);
			int y = TRANSCRIPT_IMAGE_TOP + TRANSCRIPT_EXON_HEIGHT;
			g2d.setStroke(new BasicStroke(INDICATION_STROKE, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
			if (exonsId.size() > 0) {
				g2d.setColor(colorList.get(exonsId.get(0) % colorList.size()));
			} else {
				g2d.setColor(Color.BLACK);
			}
			g2d.drawLine(x, y, (int) Math.round(centX.get(circNum)),
					(int) Math.round(centY.get(circNum)) - (int) Math.round(circRectLength / 2));
			// End Exon Line
			x = IMAGE_MARGIN_LEFT;
			if (gt.getStrand().equals("-")) {
				x += STRAND_ARROW_LENGTH;
			}
			x += (int) Math.round((circRna.getEndPoint() - basePoint) * lengthRatio);
			if (exonsId.size() > 0) {
				g2d.setColor(colorList.get(exonsId.get(exonsId.size() - 1) % colorList.size()));
			} else {
				g2d.setColor(Color.BLACK);
			}
			g2d.drawLine(x, y, (int) Math.round(centX.get(circNum)),
					(int) Math.round(centY.get(circNum)) - (int) Math.round(circRectLength / 2));

			// Left-Top Point for this CircRNA
			double circX = centX.get(circNum) - diameter / 2.0;
			double circY = centY.get(circNum) - diameter / 2.0;
			// Exon Type CircRNA
			if (gt.getCircRnas().get(circRnaId).getCircRnaType().equalsIgnoreCase("Exon")) {
				// Draw Circle
				int startAngle = 90;
				int angle = 0;
				if ((exonsId.size() > 0)
						&& (gt.getExonStarts().get(exonsId.get(0)) - Constant.ASSIGN_TOLERATION <= gt.getCircRnas()
								.get(circRnaId).getStartPoint())
						&& (gt.getCircRnas().get(circRnaId).getStartPoint() <= gt.getExonEnds().get(exonsId.get(0))
								+ Constant.ASSIGN_TOLERATION)
						&& (gt.getExonStarts().get(exonsId.get(exonsId.size() - 1)) - Constant.ASSIGN_TOLERATION <= gt
								.getCircRnas().get(circRnaId).getEndPoint())
						&& (gt.getCircRnas().get(circRnaId)
								.getEndPoint() <= gt.getExonEnds().get(exonsId.get(exonsId.size() - 1))
										+ Constant.ASSIGN_TOLERATION)) {
					// Draw only with Exon ONLY
					// RBP for this CircRNA Image
					TreeMap<String, Vector<String>> oneCircRbp = circRnasRbp.get(circRnaId);
					TreeMap<String, Vector<String>> oneCircMre = circRnasMre.get(circRnaId);
					for (int exonId : exonsId) {
						angle = exonArcsDegree.get(exonId);
						g2d.setStroke(
								new BasicStroke((float) lineStroke, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
						g2d.setColor(colorList.get(exonId % colorList.size()));
						g2d.drawArc((int) Math.round(circX), (int) Math.round(circY), (int) Math.round(diameter),
								(int) Math.round(diameter), startAngle, angle);
						// Draw Exon ID
						int exonNum = 1;
						if (gt.getStrand().equals("+")) {
							exonNum = exonId + 1;
						} else {
							exonNum = gt.getExonCount() - exonId;
						}
						g2d.setColor(Color.BLACK);
						g2d.setStroke(new BasicStroke(DEFAULT_STROKE, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
						String[] tmp = exonsInfo.get(exonId).split("-");
						int exonLen = Integer.parseInt(tmp[1]) - Integer.parseInt(tmp[0]);
						g2d.setFont(new Font("TimesRoman", Font.PLAIN, (int) Math.round(diameter / 18.0)));
						g2d.drawString(
								exonNum + "(" + exonLen
										+ ")",
								(int) (centX.get(circNum) - lineStroke / 4
										+ diameter / 2
												* Math.cos(
														((startAngle + angle / 2) % 360) * Math.PI
																/ 180)),
								(int) (centY.get(circNum) + lineStroke / 4
										- diameter / 2 * Math.sin(((startAngle + angle / 2) % 360) * Math.PI / 180)));

						// Draw RBP for this Exon
						if (null != oneCircRbp) {
							int rbpHeight = (int) Math.round(lineStroke);
							Vector<String> oneExonRbp = oneCircRbp
									.get(gt.getExonStarts().get(exonId) + "-" + gt.getExonEnds().get(exonId));
							if (null != oneExonRbp) {
								for (int r = 0; r < oneExonRbp.size(); r++) {
									String[] item = oneExonRbp.get(r).split("\t");
									double exonRelatePos = Double.parseDouble(item[5]);
									double rbpPositionAngle = (startAngle + angle * exonRelatePos) % 360;
									int rbpX = (int) Math
											.round(centX.get(circNum)
													+ (diameter + lineStroke) / 2
															* Math.cos(rbpPositionAngle / 180 * Math.PI)
													+ lineStroke / 2);
									int rbpY = (int) Math
											.round(centY.get(circNum)
													- (diameter + lineStroke) / 2
															* Math.sin(rbpPositionAngle / 180 * Math.PI)
													+ lineStroke / 2);
									drawRBP(g2d, rbpX, rbpY, (int) Math.round(rbpPositionAngle), rbpHeight);
								}
							}
						}
						// Draw Mre for this Exon
						if (null != oneCircMre) {
							Vector<String> oneExonMre = oneCircMre
									.get(gt.getExonStarts().get(exonId) + "-" + gt.getExonEnds().get(exonId));
							if (null != oneExonMre) {
								for (int r = 0; r < oneExonMre.size(); r++) {
									String[] item = oneExonMre.get(r).split("\t");
									double exonRelatePos = Double.parseDouble(item[5]);
									double mrePositionAngle = (startAngle + angle * exonRelatePos) % 360;
									int mreX1 = (int) Math.round(centX.get(circNum)
											+ (diameter - lineStroke) / 2 * Math.cos(mrePositionAngle / 180 * Math.PI));
									int mreY1 = (int) Math.round(centY.get(circNum)
											- (diameter - lineStroke) / 2 * Math.sin(mrePositionAngle / 180 * Math.PI));
									int mreX2 = (int) Math.round(centX.get(circNum)
											+ (diameter / 2 - lineStroke) * Math.cos(mrePositionAngle / 180 * Math.PI));
									int mreY2 = (int) Math.round(centY.get(circNum)
											- (diameter / 2 - lineStroke) * Math.sin(mrePositionAngle / 180 * Math.PI));
									drawMre(g2d, mreX1, mreY1, mreX2, mreY2);
								}
							}
						}

						startAngle += angle;
						startAngle %= 360;
					}

				} else if (exonArcsDegree.size() > 0) {
					// Draw with Intron and Exon
					for (int exonId : exonsId) {
						angle = exonArcsDegree.get(exonId);
						// Exon
						g2d.setStroke(
								new BasicStroke((float) lineStroke, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
						g2d.setColor(colorList.get(exonId % colorList.size()));
						g2d.drawArc((int) Math.round(circX), (int) Math.round(circY), (int) Math.round(diameter),
								(int) Math.round(diameter), startAngle, (int) Math.round(angle / 2.0));
						// Intron
						g2d.setStroke(new BasicStroke(TRANSCRIPT_INTRON_HEIGHT, BasicStroke.CAP_BUTT,
								BasicStroke.JOIN_BEVEL));
						g2d.setColor(Color.BLACK);
						g2d.drawArc((int) Math.round(circX), (int) Math.round(circY), (int) Math.round(diameter),
								(int) Math.round(diameter), (startAngle + (int) Math.round(angle / 2.0)) % 360,
								(int) Math.round(angle / 2.0));
						// Draw Exon ID
						int exonNum = 1;
						if (gt.getStrand().equals("+")) {
							exonNum = exonId + 1;
						} else {
							exonNum = gt.getExonCount() - exonId;
						}
						g2d.setColor(Color.BLACK);
						g2d.setStroke(new BasicStroke(DEFAULT_STROKE, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
						String[] tmp = exonsInfo.get(exonId).split("-");
						int exonLen = Integer.parseInt(tmp[1]) - Integer.parseInt(tmp[0]);
						g2d.setFont(new Font("TimesRoman", Font.PLAIN, (int) Math.round(diameter / 18.0)));
						g2d.drawString(
								exonNum + "(" + exonLen
										+ ")",
								(int) (centX.get(circNum) - lineStroke / 4
										+ diameter / 2
												* Math.cos(
														((startAngle + angle / 4) % 360) * Math.PI
																/ 180)),
								(int) (centY.get(circNum) + lineStroke / 4
										- diameter / 2 * Math.sin(((startAngle + angle / 4) % 360) * Math.PI / 180)));

						startAngle += angle;
						startAngle %= 360;
					}
				} else {
					// Draw with Intron ONLY
					String intronType = "Intron";
					g2d.setColor(Color.BLACK);
					g2d.setStroke(new BasicStroke(DEFAULT_STROKE, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
					g2d.setFont(new Font("TimesRoman", Font.PLAIN, (int) Math.round(diameter / 16.0)));
					g2d.drawString(intronType,
							(int) Math.round(centX.get(circNum)) - g2d.getFontMetrics().stringWidth(intronType) / 2,
							(int) (Math.round(centY.get(circNum)) - diameter / 5.0));
					g2d.setStroke(
							new BasicStroke(TRANSCRIPT_INTRON_HEIGHT, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
					g2d.drawArc((int) Math.round(circX), (int) Math.round(circY), (int) Math.round(diameter),
							(int) Math.round(diameter), 0, 360);
				}
			} else if (gt.getCircRnas().get(circRnaId).getCircRnaType().equalsIgnoreCase("Intron")) {
				String intronType = "Intron";
				g2d.setColor(Color.BLACK);
				g2d.setStroke(new BasicStroke(DEFAULT_STROKE, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
				g2d.setFont(new Font("TimesRoman", Font.PLAIN, (int) Math.round(diameter / 16.0)));
				g2d.drawString(intronType,
						(int) Math.round(centX.get(circNum) - g2d.getFontMetrics().stringWidth(intronType) / 2.0),
						(int) (Math.round(centY.get(circNum)) - diameter / 5.0));
				g2d.setStroke(new BasicStroke(TRANSCRIPT_INTRON_HEIGHT, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
				g2d.drawArc((int) Math.round(centX.get(circNum) - diameter / 2.0),
						(int) Math.round(centY.get(circNum) - diameter / 2.0), (int) Math.round(diameter),
						(int) Math.round(diameter), 0, 360);
			} else if (gt.getCircRnas().get(circRnaId).getCircRnaType().equalsIgnoreCase("Intergenic")) {
				// Intergenic Type CircRNA
				String intronType = "Intergenic";
				g2d.setColor(Color.RED);
				g2d.setStroke(new BasicStroke(DEFAULT_STROKE, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
				g2d.setFont(new Font("TimesRoman", Font.PLAIN, (int) Math.round(diameter / 16.0)));
				g2d.drawString(intronType,
						(int) Math.round(centX.get(circNum))
								- (int) Math.round(g2d.getFontMetrics().stringWidth(intronType) / 2.0),
						(int) (Math.round(centY.get(circNum)) - diameter / 5.0));
				g2d.setStroke(new BasicStroke(TRANSCRIPT_INTRON_HEIGHT, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
				g2d.drawArc((int) Math.round(centX.get(circNum) - diameter / 2.0),
						(int) Math.round(centY.get(circNum) - diameter / 2.0), (int) Math.round(diameter),
						(int) Math.round(diameter), 0, 360);
			} else {
				g2d.setColor(Color.RED);

			}

			// Draw CircRNA anotation
			CIRCRNA_INFO_FONT = (int) Math.round(diameter / 15.0);
			g2d.setFont(new Font("TimesRoman", Font.PLAIN, CIRCRNA_INFO_FONT));
			g2d.setColor(Color.BLACK);
			g2d.setStroke(new BasicStroke(DEFAULT_STROKE, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
			g2d.setFont(new Font("TimesRoman", Font.PLAIN, (int) Math.round(diameter / 14.0)));
			g2d.drawString(circRnaId.replace("|", "-"),
					(int) Math.round(centX.get(circNum) - g2d.getFontMetrics().stringWidth(circRnaId) / 2.0),
					(int) Math.round(centY.get(circNum) + circRectLength / 2.0) + ANNOTATION_FONT);

			// Draw CircRna Recurrent
			CIRCRNA_INFO_FONT = (int) Math.round(diameter / 16.0);
			g2d.setFont(new Font("TimesRoman", Font.PLAIN, CIRCRNA_INFO_FONT));
			String recSample = "recurrent sample:" + circRna.getSamples().size() + "/" + sampleNum + "="
					+ Math.round(100.0 * circRna.getSamples().size() / sampleNum) + "%";
			String recAlgorithm = "overlap algorithm:" + circRna.getCircTools().size() + "/" + toolNum + "="
					+ Math.round(100.0 * circRna.getCircTools().size() / toolNum) + "%";
			String junctionReads = "max abundance:" + circRna.getJunctionReads();
			g2d.setFont(new Font("TimesRoman", Font.PLAIN, (int) Math.round(diameter / 16.0)));
			g2d.drawString(recSample,
					(int) Math.round(centX.get(circNum) - g2d.getFontMetrics().stringWidth(recSample) / 2.0),
					(int) Math.round(centY.get(circNum) - diameter / 10.0));
			g2d.drawString(recAlgorithm,
					(int) Math.round(centX.get(circNum) - g2d.getFontMetrics().stringWidth(recAlgorithm) / 2.0),
					(int) Math.round(centY.get(circNum)));
			g2d.drawString(junctionReads,
					(int) Math.round(centX.get(circNum) - g2d.getFontMetrics().stringWidth(junctionReads) / 2.0),
					(int) Math.round(centY.get(circNum) + diameter / 10.0));

			// Draw CircRna Strand
			g2d.setStroke(new BasicStroke((float) lineStroke, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
			g2d.drawArc((int) Math.round(centX.get(circNum) - diameter / 2.0),
					(int) Math.round(centY.get(circNum) - diameter / 2.0), (int) Math.round(diameter),
					(int) Math.round(diameter), 90, 1);
			g2d.setStroke(new BasicStroke(DEFAULT_STROKE, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
			g2d.drawArc((int) Math.round(centX.get(circNum) - diameter / 2.0),
					(int) Math.round(centY.get(circNum) - diameter / 2.0), (int) Math.round(diameter),
					(int) Math.round(diameter), 0, 360);
			double thick = lineStroke / 4;
			double r = diameter / 2;
			if (gt.getCircRnas().get(circRnaId).getStrand().equals("+")) {
				// Counerclockwise
				Polygon arrow = new Polygon();
				int xP, yP, xP1, yP1, xP2, yP2;
				xP = (int) Math.round(centX.get(circNum));
				yP = (int) (centY.get(circNum) - r);
				xP1 = (int) Math.round(centX.get(circNum) + (r - thick) * Math.sin(20.0 / 180));
				yP1 = (int) Math.round(centY.get(circNum) - (r - thick) * Math.cos(20.0 / 180));
				xP2 = (int) Math.round(centX.get(circNum) + (r + thick) * Math.sin(20.0 / 180));
				yP2 = (int) Math.round(centY.get(circNum) - (r + thick) * Math.cos(20.0 / 180));
				arrow.addPoint(xP, yP);
				arrow.addPoint(xP1, yP1);
				arrow.addPoint(xP2, yP2);
				g2d.setColor(Color.BLACK);
				g2d.fill(arrow);

				arrow = new Polygon();
				xP = (int) Math.round(centX.get(circNum));
				yP = (int) (centY.get(circNum) + r);
				xP1 = (int) Math.round(centX.get(circNum) - (r - thick) * Math.sin(20.0 / 180));
				yP1 = (int) Math.round(centY.get(circNum) + (r - thick) * Math.cos(20.0 / 180));
				xP2 = (int) Math.round(centX.get(circNum) - (r + thick) * Math.sin(20.0 / 180));
				yP2 = (int) Math.round(centY.get(circNum) + (r + thick) * Math.cos(20.0 / 180));
				arrow.addPoint(xP, yP);
				arrow.addPoint(xP1, yP1);
				arrow.addPoint(xP2, yP2);
				g2d.setColor(Color.BLACK);
				g2d.fill(arrow);
			} else {
				// Clockwise
				Polygon arrow = new Polygon();
				int xP, yP, xP1, yP1, xP2, yP2;
				xP = (int) Math.round(centX.get(circNum));
				yP = (int) (centY.get(circNum) - r);
				xP1 = (int) Math.round(centX.get(circNum) - (r - thick) * Math.sin(20.0 / 180));
				yP1 = (int) Math.round(centY.get(circNum) - (r - thick) * Math.cos(20.0 / 180));
				xP2 = (int) Math.round(centX.get(circNum) - (r + thick) * Math.sin(20.0 / 180));
				yP2 = (int) Math.round(centY.get(circNum) - (r + thick) * Math.cos(20.0 / 180));
				arrow.addPoint(xP, yP);
				arrow.addPoint(xP1, yP1);
				arrow.addPoint(xP2, yP2);
				g2d.setColor(Color.BLACK);
				g2d.fill(arrow);

				arrow = new Polygon();
				xP = (int) Math.round(centX.get(circNum));
				yP = (int) (centY.get(circNum) + r);
				xP1 = (int) Math.round(centX.get(circNum) + (r - thick) * Math.sin(20.0 / 180));
				yP1 = (int) Math.round(centY.get(circNum) + (r - thick) * Math.cos(20.0 / 180));
				xP2 = (int) Math.round(centX.get(circNum) + (r + thick) * Math.sin(20.0 / 180));
				yP2 = (int) Math.round(centY.get(circNum) + (r + thick) * Math.cos(20.0 / 180));
				arrow.addPoint(xP, yP);
				arrow.addPoint(xP1, yP1);
				arrow.addPoint(xP2, yP2);
				g2d.setColor(Color.BLACK);
				g2d.fill(arrow);
			}
		}
	}

	public void selectOneCircRna(String circRnaId) {
		gt = gtBackup.deepClone();
		Iterator<String> it = gt.getCircRnas().keySet().iterator();
		while (it.hasNext()) {
			String id = it.next();
			if (!id.equalsIgnoreCase(circRnaId)) {
				it.remove();
			}
		}
	}

	public void selectAllCircRnas() {
		gt = gtBackup.deepClone();
	}

	private void drawRBP(Graphics2D g2d, int centX, int centY, int angle, int height) {
		g2d.setColor(Color.RED);
		g2d.setStroke(new BasicStroke(DEFAULT_STROKE, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
		g2d.fillArc(centX - height, centY - height, height, height, angle - 15, 30);
	}

	private void drawMre(Graphics2D g2d, int x1, int y1, int x2, int y2) {
		g2d.setColor(Color.BLUE);
		g2d.setStroke(new BasicStroke(MRE_STROKE, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
		g2d.drawLine(x1, y1, x2, y2);
	}

	public void saveImage(GeneTranscript gt, File outFile, String type, int w, int h) {
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		this.drawCircRnaImage(image, gt);
		Graphics2D g2d = image.createGraphics();
		g2d.drawImage(image, 0, 0, this);
		try {
			ImageIO.write(image, type, outFile);
			JOptionPane.showMessageDialog(CircView.frame, "Save Image Successfully!");
		} catch (IOException e) {
			CircView.log.warn(e.getMessage());
		}
	}

	public void queryRbpData(String species, GeneTranscript gt) {
		// Ready for Query, get the Unique Exon start-end Value
		TreeMap<String, String> queryItem = new TreeMap<String, String>();
		for (String circRnaId : gt.getCircRnas().keySet()) {
			CircRna circRna = gt.getCircRnas().get(circRnaId);
			long start = circRna.getStartPoint();
			long end = circRna.getEndPoint();
			for (int j = 0; j < gt.getExonCount(); j++) {
				long actualStart = -1;
				long actualEnd = -1;
				long exonStart = gt.getExonStarts().get(j);
				long exonEnd = gt.getExonEnds().get(j);
				if ((start <= exonStart) && (exonEnd <= end)) {
					actualStart = exonStart;
					actualEnd = exonEnd;
				} else if ((exonStart < start) && (start < exonEnd) && (exonEnd <= end)) {
					actualStart = start;
					actualEnd = exonEnd;
				} else if ((start <= exonStart) && (exonStart < end) && (end < exonEnd)) {
					actualStart = exonStart;
					actualEnd = end;
				} else if ((exonStart < start) && (end < exonEnd)) {
					actualStart = start;
					actualEnd = end;
				}
				if (actualStart > 0) {
					queryItem.put(actualStart + "-" + actualEnd, exonStart + "-" + exonEnd);
				}
			}
		}

		// Query RBP Info from database
		Vector<Long> starts = new Vector<Long>();
		Vector<Long> ends = new Vector<Long>();
		for (String startEnd : queryItem.keySet()) {
			String[] tmp = startEnd.split("-");
			starts.add(Long.parseLong(tmp[0]));
			ends.add(Long.parseLong(tmp[1]));
		}
		Vector<String> queryResult = new Vector<String>();
		try {
			String tableName = "rbp_" + DbUtil.species2TableName(species);
			queryResult = DbUtil.selectRbp(conn, tableName, gt.getChrom(), starts, ends);
			if (null == queryResult) {
				return;
			}
		} catch (SQLException e) {
			CircView.log.warn(e.getMessage());
		}

		for (String circRnaId : gt.getCircRnas().keySet()) {
			CircRna circRna = gt.getCircRnas().get(circRnaId);
			long start = circRna.getStartPoint();
			long end = circRna.getEndPoint();
			// Exon start-end --> server RBP infos
			TreeMap<String, Vector<String>> oneCircRbp = new TreeMap<String, Vector<String>>();
			for (int j = 0; j < gt.getExonCount(); j++) {
				Vector<String> oneExonRbp = null;
				long actualStart = -1;
				long actualEnd = -1;
				long exonStart = gt.getExonStarts().get(j);
				long exonEnd = gt.getExonEnds().get(j);
				if ((start <= exonStart) && (exonEnd <= end)) {
					actualStart = exonStart;
					actualEnd = exonEnd;
				} else if ((exonStart < start) && (start < exonEnd) && (exonEnd <= end)) {
					actualStart = start;
					actualEnd = exonEnd;
				} else if ((start <= exonStart) && (exonStart < end) && (end < exonEnd)) {
					actualStart = exonStart;
					actualEnd = end;
				} else if ((exonStart < start) && (end < exonEnd)) {
					actualStart = start;
					actualEnd = end;
				}

				for (int r = 0; r < queryResult.size(); r++) {
					String oneRbp = queryResult.get(r);
					String[] tmp = oneRbp.split("\t");
					long rbpStart = Long.parseLong(tmp[1]);
					long rbpEnd = Long.parseLong(tmp[2]);
					if (actualStart <= rbpStart && rbpEnd <= actualEnd) {
						oneRbp += "\t" + 1.0 * (rbpStart - actualStart) / (actualEnd - actualStart);
						if (null == oneExonRbp) {
							oneExonRbp = new Vector<String>();
						}
						oneExonRbp.addElement(oneRbp);
					}
				}
				if (null != oneExonRbp) {
					oneCircRbp.put(actualStart + "-" + actualEnd, oneExonRbp);
				}
			}
			circRnasRbp.put(circRnaId, oneCircRbp);
		}
	}

	public void queryMreData(String species, GeneTranscript gt) {
		// Ready for Query, get the Unique Exon start-end Value
		TreeMap<String, String> queryItem = new TreeMap<String, String>();
		for (String circRnaId : gt.getCircRnas().keySet()) {
			CircRna circRna = gt.getCircRnas().get(circRnaId);
			long start = circRna.getStartPoint();
			long end = circRna.getEndPoint();
			for (int j = 0; j < gt.getExonCount(); j++) {
				long actualStart = -1;
				long actualEnd = -1;
				long exonStart = gt.getExonStarts().get(j);
				long exonEnd = gt.getExonEnds().get(j);
				if ((start <= exonStart) && (exonEnd <= end)) {
					actualStart = exonStart;
					actualEnd = exonEnd;
				} else if ((exonStart < start) && (start < exonEnd) && (exonEnd <= end)) {
					actualStart = start;
					actualEnd = exonEnd;
				} else if ((start <= exonStart) && (exonStart < end) && (end < exonEnd)) {
					actualStart = exonStart;
					actualEnd = end;
				} else if ((exonStart < start) && (end < exonEnd)) {
					actualStart = start;
					actualEnd = end;
				}
				if (actualStart > 0) {
					queryItem.put(actualStart + "-" + actualEnd, exonStart + "-" + exonEnd);
				}
			}
		}

		// Query Mre Info from database
		Vector<Long> starts = new Vector<Long>();
		Vector<Long> ends = new Vector<Long>();
		for (String startEnd : queryItem.keySet()) {
			String[] tmp = startEnd.split("-");
			starts.add(Long.parseLong(tmp[0]));
			ends.add(Long.parseLong(tmp[1]));
		}
		Vector<String> queryResult = new Vector<String>();
		try {
			String tableName = "mre_" + DbUtil.species2TableName(species);
			queryResult = DbUtil.selectMre(conn, tableName, gt.getChrom(), starts, ends);
			if (null == queryResult) {
				return;
			}
		} catch (SQLException e) {
			CircView.log.warn(e.getMessage());
		}

		for (String circRnaId : gt.getCircRnas().keySet()) {
			CircRna circRna = gt.getCircRnas().get(circRnaId);
			long start = circRna.getStartPoint();
			long end = circRna.getEndPoint();
			// Exon start-end --> server MRE infos
			TreeMap<String, Vector<String>> oneCircMre = new TreeMap<String, Vector<String>>();
			for (int j = 0; j < gt.getExonCount(); j++) {
				Vector<String> oneExonMre = null;
				long actualStart = -1;
				long actualEnd = -1;
				long exonStart = gt.getExonStarts().get(j);
				long exonEnd = gt.getExonEnds().get(j);
				if ((start <= exonStart) && (exonEnd <= end)) {
					actualStart = exonStart;
					actualEnd = exonEnd;
				} else if ((exonStart < start) && (start < exonEnd) && (exonEnd <= end)) {
					actualStart = start;
					actualEnd = exonEnd;
				} else if ((start <= exonStart) && (exonStart < end) && (end < exonEnd)) {
					actualStart = exonStart;
					actualEnd = end;
				} else if ((exonStart < start) && (end < exonEnd)) {
					actualStart = start;
					actualEnd = end;
				}

				for (int r = 0; r < queryResult.size(); r++) {
					String oneMre = queryResult.get(r);
					String[] tmp = oneMre.split("\t");
					long mreStart = Long.parseLong(tmp[1]);
					long mreEnd = Long.parseLong(tmp[2]);
					if (actualStart <= mreStart && mreEnd <= actualEnd) {
						oneMre += "\t" + 1.0 * (mreStart - actualStart) / (actualEnd - actualStart);
						if (null == oneExonMre) {
							oneExonMre = new Vector<String>();
						}
						oneExonMre.addElement(oneMre);
					}
				}
				if (null != oneExonMre) {
					oneCircMre.put(actualStart + "-" + actualEnd, oneExonMre);
				}
			}
			circRnasMre.put(circRnaId, oneCircMre);
		}
	}

	public void clearRbp() {
		circRnasRbp.clear();
	}

	public void clearMre() {
		circRnasMre.clear();
	}

	public String getDetails(GeneTranscript gt) {
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
		String details = "";
		if (null != gt) {
			// Gene Transcript Info
			details += "########## Gene Transcript ##########\n";
			details += gt.getGeneName() + "\t" + gt.getTranscriptName() + "\t" + gt.getChrom() + "\t" + gt.getStrand()
					+ "\t" + gt.getTotalJunctionReads() + "\t" + gt.getTxStart() + "\t" + gt.getTxEnd() + "\t"
					+ gt.getCdsStart() + "\t" + gt.getCdsEnd() + "\t" + gt.getExonCount() + "\t";
			for (int i = 0; i < gt.getExonCount(); i++) {
				details += gt.getExonStarts().get(i) + ",";
			}
			details += "\t";
			for (int i = 0; i < gt.getExonCount(); i++) {
				details += gt.getExonEnds().get(i) + ",";
			}
			details += "\n\n";

			// CircRNAs Info
			details += "########## CircRNA ##########\n";
			for (String circRnaId : gt.getCircRnas().keySet()) {
				CircRna circRna = gt.getCircRnas().get(circRnaId);
				details += circRna.getChrom() + "\t" + circRna.getStartPoint() + "\t" + circRna.getEndPoint() + "\t"
						+ circRna.getStrand() + "\t" + circRna.getJunctionReads() + "\t" + circRna.getCircRnaType()
						+ "\t" + circRna.getRegion() + "\t";
				for (String sampleName : circRna.getSamples().keySet()) {
					details += sampleName + ',';
				}
				details += "\t";
				for (String circTool : circRna.getCircTools().keySet()) {
					details += circTool + ',';
				}
				details += "\t";
				for (String fileName : circRna.getFiles().keySet()) {
					details += fileName + ',';
				}
				details += "\n";
			}
			details += "\n";

			// RBP Info
			if (circRnasRbp.size() > 0) {
				details += "########## RBP ##########\n";
				for (String circRnaId : gt.getCircRnas().keySet()) {
					TreeMap<String, Vector<String>> oneCircRnaRbp = circRnasRbp.get(circRnaId);
					if (null != oneCircRnaRbp) {
						for (String exonId : oneCircRnaRbp.keySet()) {
							Vector<String> oneExonRbp = oneCircRnaRbp.get(exonId);
							for (int i = 0; i < oneExonRbp.size(); i++) {
								String[] item = oneExonRbp.get(i).split("\t");
								for (int j = 0; j < item.length - 1; j++) {
									details += item[j] + "\t";
								}
								details += "\n";
							}
						}
					}
				}
				details += "\n";
			}

			// MRE Info
			if (circRnasMre.size() > 0) {
				details += "########## MRE ##########\n";
				for (String circRnaId : gt.getCircRnas().keySet()) {
					TreeMap<String, Vector<String>> oneCircRnaMre = circRnasMre.get(circRnaId);
					if (null != oneCircRnaMre) {
						for (String exonId : oneCircRnaMre.keySet()) {
							Vector<String> oneExonMre = oneCircRnaMre.get(exonId);
							for (int i = 0; i < oneExonMre.size(); i++) {
								String[] item = oneExonMre.get(i).split("\t");
								for (int j = 0; j < item.length - 1; j++) {
									details += item[j] + "\t";
								}
								details += "\n";
							}
						}
					}
				}
			}
		}
		return details;
	}

	public String getCircRnaInfo(String circRnaId) {
		String details = "";
		CircRna circRna = gt.getCircRnas().get(circRnaId);
		if (null != circRna) {
			// CircRNAs Info
			details += circRna.getChrom() + "\t" + circRna.getStartPoint() + "\t" + circRna.getEndPoint() + "\t"
					+ circRna.getStrand() + "\t" + circRna.getJunctionReads() + "\n";
			details += "Exon: ";
			Vector<Integer> index = new Vector<Integer>();
			for (int i = 0; i < gt.getExonStarts().size(); i++) {
				if ((circRna.getStartPoint() <= gt.getExonStarts().get(i))
						&& (gt.getExonEnds().get(i) <= circRna.getEndPoint())) {
					index.addElement(i);
				} else if ((gt.getExonStarts().get(i) <= circRna.getStartPoint())
						&& (circRna.getStartPoint() <= gt.getExonEnds().get(i))) {
					index.addElement(i);
				} else if ((gt.getExonStarts().get(i) <= circRna.getEndPoint())
						&& (circRna.getEndPoint() <= gt.getExonEnds().get(i))) {
					index.addElement(i);
				}
			}
			for (int i : index) {
				details += gt.getExonStarts().get(i) + ",";
			}
			details += "\t";
			for (int i : index) {
				details += gt.getExonEnds().get(i) + ",";
			}
			details += "\n";
			details += "Type: " + circRna.getCircRnaType() + "\n";
			details += "Region: " + circRna.getRegion() + "\n";
			details += "Sample: ";
			for (String sampleName : circRna.getSamples().keySet()) {
				details += sampleName + ',';
			}
			details += "\n";
			details += "Algorithm: ";
			for (String circTool : circRna.getCircTools().keySet()) {
				details += circTool + ',';
			}
			details += "\n";
			details += "File: ";
			for (String fileName : circRna.getFiles().keySet()) {
				details += fileName + ',';
			}
			details += "\n";
		}
		return details;
	}

	public boolean isContainRbp() {
		if (circRnasRbp.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isContainMre() {
		if (circRnasMre.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public BufferedImage getCircRnaImage() {
		return circRnaImage;
	}

	public void setCircRnaImage(BufferedImage circRnaImage) {
		this.circRnaImage = circRnaImage;
	}

	public TreeMap<String, TreeMap<String, Vector<String>>> getCircRnasRbp() {
		return circRnasRbp;
	}

	public void setCircRnasRbp(TreeMap<String, TreeMap<String, Vector<String>>> circRnasRbp) {
		this.circRnasRbp = circRnasRbp;
	}

	public TreeMap<String, TreeMap<String, Vector<String>>> getCircRnasMre() {
		return circRnasMre;
	}

	public void setCircRnasMre(TreeMap<String, TreeMap<String, Vector<String>>> circRnasMre) {
		this.circRnasMre = circRnasMre;
	}

	public GeneTranscript getGtBackup() {
		return gtBackup;
	}

	public Vector<Double> getCircX() {
		return circX;
	}

	public void setCircX(Vector<Double> circX) {
		this.circX = circX;
	}

	public Vector<Double> getCircY() {
		return circY;
	}

	public void setCircY(Vector<Double> circY) {
		this.circY = circY;
	}

	public double getCircR() {
		return circR;
	}

	public void setCircR(double circR) {
		this.circR = circR;
	}
}
