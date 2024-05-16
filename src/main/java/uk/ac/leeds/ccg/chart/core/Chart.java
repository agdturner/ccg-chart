package uk.ac.leeds.ccg.chart.core;

import ch.obermuhlner.math.big.BigRational;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.StreamPrintService;
import javax.print.StreamPrintServiceFactory;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.swing.JFrame;
import uk.ac.leeds.ccg.chart.data.Chart_Data;
import uk.ac.leeds.ccg.chart.data.BigDecimal2;
import uk.ac.leeds.ccg.chart.execution.Chart_Runnable;
import uk.ac.leeds.ccg.generic.core.Generic_Environment;
import uk.ac.leeds.ccg.generic.execution.Generic_Execution;
import uk.ac.leeds.ccg.generic.visualisation.Generic_Visualisation;

/**
 * An class for creating 2D plot visualisations.
 */
public abstract class Chart extends Chart_Runnable
        implements Chart_Drawable, Runnable {

    protected final Generic_Environment env;
    protected final Generic_Execution exec;
    protected final Generic_Visualisation vis;

    protected Chart_Data data;
    protected String format;
    protected Path file;
    protected Graphics2D g2image;
    protected Graphics2D g2;
    protected BufferedImage bi;

    public abstract Chart_Data getData();

    /**
     * Metrics to do with the font used for text in the plot.
     */
    protected FontMetrics fontMetrics;

    /**
     * For controlling if origin lines (lines at Y = 0 or X = 0) are drawn on
     * the plot.
     */
    protected boolean drawOriginLinesOnPlot;

    /**
     * For storing the title for the plot.
     */
    protected String title;

    /**
     * For storing the width of the image to be created.
     */
    protected int imageWidth;

    /**
     * For storing the height of the image to be created.
     */
    protected int imageHeight;

    /**
     * For storing the width in pixels of the data section part of the image.
     */
    protected int dataWidth;

    /**
     * For storing the height in pixels of the data section part of the image.
     */
    protected int dataHeight;

    /**
     * For storing the top row index of the data section in the image.
     */
    protected int dataStartRow;

    /**
     * For storing the middle row index of the data section in the image.
     */
    protected int dataMiddleRow;

    /**
     * For storing the bottom row index of the data section in the image.
     */
    protected int dataEndRow;

    /**
     * For storing the left column index of the data section in the image.
     */
    protected int dataStartCol;

    /**
     * For storing the right column index of the data section in the image.
     */
    protected int dataEndCol;

    /**
     * For storing the height in pixels of the x axis.
     */
    protected int xAxisHeight;

    /**
     * For storing the width in pixels of the y axis.
     */
    protected int yAxisWidth;

    /**
     * For storing the main x axis label text.
     */
    protected String xAxisLabel;

    /**
     * For storing the main y axis label text.
     */
    protected String yAxisLabel;

    /**
     * For storing the extra width in pixels to the left of the data part of the
     * plot.
     */
    protected int extraWidthLeft;

    /**
     * For storing the extra width in pixels to the right of the data part of
     * the plot.
     */
    protected int extraWidthRight;

    /**
     * For storing the extra width in pixels above the data part of the plot.
     */
    protected int extraHeightTop;

    /**
     * For storing the extra width in pixels below the data part of the plot.
     */
    protected int extraHeightBottom;

    /**
     * For storing the maximum value of x in the data area.
     */
    protected BigRational maxX;

    /**
     * For storing the minimum value of x in the data area.
     */
    protected BigRational minX;

    /**
     * For storing the maximum value of y in the data area.
     */
    protected BigRational maxY;

    /**
     * For storing the minimum value of y in the data area.
     */
    protected BigRational minY;

    /**
     * The order of magnitude for the precision of x value strings.
     */
    protected int oomx;

    /**
     * The order of magnitude for the precision of y value strings.
     */
    protected int oomy;

    /**
     * The rounding mode for any rounding necessary for display of numerical 
     * values.
     */
    protected RoundingMode rm;

    /**
     * cellHeight is for storing the height of a pixel in the data units of y
     */
    private BigRational cellHeight;

    /**
     * cellWidth is for storing the width of a pixel in the data units of x
     */
    private BigRational cellWidth;

    /**
     * originRow the row index on which the origin is located (y = 0).
     */
    protected int originRow;

    /**
     * originCol; the column index on which the origin is located (x = 0).
     */
    protected int originCol;

    /**
     * The height of the legend.
     */
    protected int legendHeight;

    /**
     * The width of the legend.
     */
    protected int legendWidth;

    /**
     * Whether to add a legend.
     */
    protected boolean addLegend;

    //MediaTracker mediaTracker;
    private int ageInterval;
    private Integer startAgeOfEndYearInterval;

    protected transient ExecutorService executorService;
    public Chart_Canvas Canvas;
    public Future future;

    public Chart(Generic_Environment e) {
        this(e, 0);
    }

    public Chart(Generic_Environment e, int runID) {
        super(runID);
        this.env = e;
        exec = new Generic_Execution(e);
        vis = new Generic_Visualisation(e);
    }

    protected ExecutorService getExecutorService() {
        if (executorService == null) {
            //executorService = Executors.newFixedThreadPool(6);
            // The advantage of the Single ThreadExecutor is that it's queue is 
            // effectively unlimited.
            executorService = Executors.newSingleThreadExecutor();
        }
        return executorService;
    }

    public void setData(Chart_Data data) {
        initData(data);
        initialiseParameters(data);
    }

    public void initData(Chart_Data data) {
        this.data = data;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Path getFile() {
        return file;
    }

    public void setFile(Path file) {
        this.file = file;
    }

    /**
     * @param data The data from which parameters will be initialised.
     */
    public void initialiseParameters(Chart_Data data) {
        minX = data.minX;
        maxX = data.maxX;
        minY = data.minY;
        maxY = data.maxY;
        setCellHeight();
        setCellWidth();
        setOriginRow();
        setOriginCol();
    }

    public Graphics2D getG2image() {
        return g2image;
    }

    protected void setG2image(Graphics2D g2image) {
        this.g2image = g2image;
    }

    public Graphics2D getG2() {
        return g2;
    }

    protected void setG2(Graphics2D g2) {
        this.g2 = g2;
    }

    public BufferedImage getBi() {
        return bi;
    }

    public void setBi(BufferedImage bufferedImage) {
        this.bi = bufferedImage;
    }

    public boolean isDrawOriginLinesOnPlot() {
        return drawOriginLinesOnPlot;
    }

    public void setDrawOriginLinesOnPlot(boolean drawOriginLinesOnPlot) {
        this.drawOriginLinesOnPlot = drawOriginLinesOnPlot;
    }

    public FontMetrics getFontMetrics() {
        return fontMetrics;
    }

    protected void setFontMetrics(FontMetrics fontMetrics) {
        this.fontMetrics = fontMetrics;
    }

    public int getDataWidth() {
        if (dataWidth < 1) {
            return 1;
        }
        return dataWidth;
    }

    public int getDataHeight() {
        if (dataHeight < 1) {
            //dataHeight = getImageHeight();
            dataHeight = 1;
        }
        return dataHeight;
    }

    protected RoundingMode getDefaultRoundingMode() {
        return RoundingMode.HALF_DOWN;
    }

    public RoundingMode getRoundingMode() {
        if (rm == null) {
            return getDefaultRoundingMode();
        }
        return rm;
    }

    public BigRational getCellHeight() {
        if (cellHeight == null) {
            return BigRational.valueOf(0);
        }
        return cellHeight;
    }

    public BigRational getCellWidth() {
        if (cellWidth == null) {
            return BigRational.valueOf(0);
        }
        return cellWidth;
    }

    public int getAgeInterval() {
        return ageInterval;
    }

    protected void setAgeInterval(int ageInterval) {
        this.ageInterval = ageInterval;
    }

    public int getStartAgeOfEndYearInterval() {
        return startAgeOfEndYearInterval;
    }

    protected void setStartAgeOfEndYearInterval(Integer startAgeOfEndYearInterval) {
        this.startAgeOfEndYearInterval = startAgeOfEndYearInterval;
    }

    /**
     * For initialising key variables.
     *
     * @param es What {@link #executorService} is set to.
     * @param file What {@link #file} is set to.
     * @param format What {@link #format} is set to.
     * @param title What {@link #title} is set to.
     * @param dataWidth What {@link #dataWidth} is set to.
     * @param dataHeight What {@link #dataHeight} is set to.
     * @param xAxisLabel What {@link #xAxisLabel} is set to.
     * @param yAxisLabel What {@link #yAxisLabel} is set to.
     * @param drawOriginLinesOnPlot What {@link #drawOriginLinesOnPlot} is set
     * to.
     * @param oomx The order of magnitude for rounding precision for x values.
     * @param oomy The order of magnitude for rounding precision for y values.
     * @param rm What {@link #rm} is set to.
     */
    protected final void init(ExecutorService es, Path file, String format,
            String title, int dataWidth, int dataHeight, String xAxisLabel,
            String yAxisLabel, boolean drawOriginLinesOnPlot, int oomx, 
            int oomy, RoundingMode rm) {
        this.executorService = es;
        this.file = file;
        this.format = format;
        this.title = title;
        // initialise this.imageWidth
        this.imageWidth = dataWidth;
        // initialise this.imageHeight
        this.imageHeight = dataHeight;
        this.dataWidth = dataWidth;
        this.dataHeight = dataHeight;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
        this.drawOriginLinesOnPlot = drawOriginLinesOnPlot;
        this.dataStartRow = 0;//extraHeightTop + boundaryThickness;
        this.dataMiddleRow = dataStartRow + dataHeight / 2;
        this.dataEndRow = dataStartRow + dataHeight;
        this.dataStartCol = 0;
        this.dataEndCol = dataStartCol + dataWidth;
        this.oomx = oomx;
        this.oomy = oomy;
        this.rm = rm;
//        if (data == null) {
//            setData(getDefaultData());
//        } else {
//            initialiseParameters(data);
//        }
    }

    protected void resize(JFrame f) {
        f.pack();
        f.setSize(imageWidth, imageHeight);
    }

    public void initG2(Graphics g) {
        g2 = (Graphics2D) g;
    }

    public void initFontMetrics() {
        if (fontMetrics == null) {
            if (g2 != null) {
                fontMetrics = g2.getFontMetrics();
            }
            Font f = Generic_Visualisation.getDefaultFont();
            fontMetrics = new Canvas().getFontMetrics(f);
        }
    }

    public void initG2Image() {
        bi = new BufferedImage(imageWidth, imageHeight,
                BufferedImage.TYPE_INT_ARGB);
        g2image = (Graphics2D) bi.getGraphics();
        g2image.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        //mediaTracker = new MediaTracker(this);
        //mediaTracker.addImage(bi, 0);
    }

    public BigRational imageRowToYCoordinate(double row) {
        return dataRowToYCoordinate(row - dataStartRow);
        //return BigDecimal.valueOf(dataHeight - row).multiply(cellHeight).subtract(halfCellHeight);
        //return BigDecimal.valueOf(dataHeight - row).multiply(cellHeight);
    }

    public BigRational imageColToXCoordinate(double col) {
        return dataColToXCoordinate(col - dataStartCol);
//        //return BigDecimal.valueOf(dataWidth - col).multiply(cellWidth).add(this.minX);
//        return BigDecimal.valueOf(dataWidth - col).multiply(cellWidth);
    }

    /**
     *
     * @param row
     * @return
     */
    public BigRational dataRowToYCoordinate(double row) {
        if (data.minY != null) {
            return BigRational.valueOf(getDataHeight() - row)
                    .multiply(getCellHeight()).add(data.minY);
        }
        return BigRational.ONE; // default value
    }

    public BigRational dataColToXCoordinate(double col) {
        if (data.minX != null) {
            return BigRational.valueOf(col).multiply(getCellWidth()).add(data.minX);
        }
        return BigRational.ONE; // default value
    }

    /**
     * Calculates and returns the column in the image for the data with value
     * titleTextWidth RoundingMode roundingMode is used.
     *
     * @param x The x value.
     * @return the column in the image for the data with value titleTextWidth
     */
    public int getCol(BigRational x) {
        int col = 0;
        BigRational cw = getCellWidth();
        if (data.minX != null) {
            if (cw.compareTo(BigRational.ZERO) != 0) {
                col = x.subtract(data.minX).divide(cw).integerPart().toBigDecimal().intValue();
            }
        }
        col += dataStartCol;
        return col;
    }

    /**
     * Calculates and returns the row in the image for the data with value
     * titleTextHeight. RoundingMode roundingMode is used.
     *
     * @param y The y value.
     * @return the row in the image for the data with value titleTextHeight
     */
    public int getRow(BigRational y) {
        int row = 0;
        BigRational ch = getCellHeight();
        if (data.minY != null) {
            if (ch != null) {
                if (ch.compareTo(BigRational.ZERO) != 0) {
                    row = getDataHeight()
                            - y.subtract(data.minY).divide(getCellHeight()).integerPart().toBigDecimal().intValue();
                }
            }
        }
        row += dataStartRow;
        return row;
    }

    /**
     * Calculates and returns the row and column in the image for the data at
     * coordinate x, y as a Point2D.Double.
     *
     * @param x The x value.
     * @param y The y value.
     * @return a Point2D.Double located at pixel(col, row)
     */
    public Point2D coordinateToScreen(BigRational x, BigRational y) {
        Point2D r = new Point2D.Double();
        int row = getRow(y);
        int col = getCol(x);
        r.setLocation(col, row);
        return r;
    }

    public void setCellHeight() {
        if (data.minY == null) {
            cellHeight = BigRational.valueOf(2);
        } else {
            cellHeight = data.maxY.subtract(data.minY).divide(BigRational.valueOf(getDataHeight()));
        }
    }

    public void setCellWidth() {
        if (data.minX == null) {
            cellWidth = BigRational.valueOf(2);
        } else {
            cellWidth = data.maxX.subtract(data.minX).divide(BigRational.valueOf(getDataWidth()));
        }
    }

    public void setOriginRow() {
        if (data.maxY == null) {
            originRow = dataEndRow;
        } else {
            if (data.maxY.compareTo(BigRational.ZERO) == 0) {
                originRow = dataEndRow;
            } else {
                if (cellHeight.compareTo(BigRational.ZERO) == 0) {
                    originRow = dataEndRow;
                } else {
                    originRow = getRow(BigRational.ZERO);
                }
            }
        }
    }

    public abstract void setOriginCol();

    /**
     * Sets the Y axis width and increases {@link imageWidth} and
     * {@link extraWidthLeft} as appropriate.
     *
     * @param width The width to set.
     */
    public void setYAxisWidth(int width) {
        //if (width > 0) {
        if (width > yAxisWidth) {
            int diff = width - yAxisWidth;
            originCol += diff;
            dataStartCol += diff;
            //dataMiddleCol += diff;
            dataEndCol += diff;
            yAxisWidth = width;
            if (width > extraWidthLeft) {
                imageWidth += diff;
                extraWidthLeft = width;
            }
        }
    }

    /**
     * Sets the X axis height and increases {@link imageHeight} and
     * {@link extraHeightBottom} as appropriate.
     *
     * @param height The height to set.
     */
    public void setXAxisHeight(int height) {
        //if (height > 0) {
        if (height > xAxisHeight) {
            int diff = height - xAxisHeight;
            xAxisHeight = height;
            if (height > extraHeightBottom) {
                imageHeight += diff;
                extraHeightBottom = height;
            }
        }
    }

//    public void setLegendHeight(int height) {
//        if (height > 0) {
//            int diff = height - this.legendHeight;
//            imageHeight += diff;
//            this.legendHeight = height;
//        }
//    }
    public void setPaint(Color c) {
//        g2.setPaint(c);
//        g2image.setPaint(c);
        if (g2 != null) {
            g2.setPaint(c);
        }
        if (g2image != null) {
            g2image.setPaint(c);
        }
    }

    public void draw(Line2D line) {
//        g2.draw(line);
//        g2image.draw(line);
        if (g2 != null) {
            g2.draw(line);
        }
        if (g2image != null) {
            g2image.draw(line);
        }
    }

    public void draw(
            Point2D point) {
//        // draw as a line with zero length
//        draw(new Line2D.Double(point, point));
        // draw as an x cross
        int crossLength = 4;
        Point2D a = new Point2D.Double((int) point.getX() - crossLength,
                (int) point.getY() - crossLength);
        Point2D b = new Point2D.Double((int) point.getX() + crossLength,
                (int) point.getY() + crossLength);
        draw(new Line2D.Double(a, b));
        Point2D c = new Point2D.Double((int) point.getX() - crossLength,
                (int) point.getY() + crossLength);
        Point2D d = new Point2D.Double((int) point.getX() + crossLength,
                (int) point.getY() - crossLength);
        draw(new Line2D.Double(c, d));
//        // draw as an x cross
//        double crossLength = 1.0d;
//        Point2D a = new Point2D.Double(
//                point.getX() - crossLength, 
//                point.getY() - crossLength);
//        Point2D b = new Point2D.Double(
//                point.getX() + crossLength, 
//                point.getY() + crossLength);
//        draw(new Line2D.Double(a, b));
//        Point2D c = new Point2D.Double(
//                point.getX() - crossLength,
//                point.getY() + crossLength);
//        Point2D d = new Point2D.Double(
//                point.getX() + crossLength,
//                point.getY() - crossLength);
//        draw(new Line2D.Double(c, d));
    }

    public void drawString(String text, int col, int row) {
        if (g2 != null) {
            g2.drawString(text, col, row);
        }
        if (g2image != null) {
            g2image.drawString(text, col, row);
        }
    }

    public void fillRect(int col, int row, int width, int height) {
        if (g2 != null) {
            g2.fillRect(col, row, width, height);
        }
        if (g2image != null) {
            g2image.fillRect(col, row, width, height);
        }
    }

    public void draw(Rectangle2D aRectangle2D) {
        if (g2 != null) {
            g2.draw(aRectangle2D);
        }
        if (g2image != null) {
            g2image.draw(aRectangle2D);
        }
    }

    public void transform(AffineTransform aAffineTransform) {
        if (g2 != null) {
            g2.transform(aAffineTransform);
        }
        if (g2image != null) {
            g2image.transform(aAffineTransform);
        }
    }

    public void setTransform(AffineTransform aAffineTransform) {
        if (g2 != null) {
            g2.setTransform(aAffineTransform);
        }
        if (g2image != null) {
            g2image.setTransform(aAffineTransform);
        }
    }

    public int getDefaultScaleTickLength() {
        return 5;
    }

    public int getDefaultScaleTickAndTextSeparation() {
        return 3;
    }

    public int getDefaultPartTitleGap() {
        return 2;
    }

    public void drawAxes(int interval, int startAgeOfEndYearInterval) {
        int scaleTickLength = getDefaultScaleTickLength();
        int scaleTickAndTextSeparation = getDefaultScaleTickAndTextSeparation();
        int partTitleGap = getDefaultPartTitleGap();
        int textHeight = getTextHeight();
        int seperationDistanceOfAxisAndData = textHeight;
        // Draw Y axis
        int[] yAxisDimensions = drawYAxis(interval, textHeight,
                startAgeOfEndYearInterval, scaleTickLength,
                scaleTickAndTextSeparation, partTitleGap,
                seperationDistanceOfAxisAndData);
        int yAxisExtraWidthLeft = yAxisDimensions[0];
        if (yAxisExtraWidthLeft > extraWidthLeft) {
            int diff = yAxisExtraWidthLeft - extraWidthLeft;
            imageWidth += diff;
            dataStartCol += diff;
            dataEndCol += diff;
            extraWidthLeft = yAxisExtraWidthLeft;
            setOriginCol();
        }
        yAxisWidth = yAxisExtraWidthLeft;
        // Draw X axis
        int[] xAxisDimensions = drawXAxis(textHeight, scaleTickLength,
                scaleTickAndTextSeparation, partTitleGap,
                seperationDistanceOfAxisAndData);
        int xAxisExtraWidthLeft = xAxisDimensions[0];
        int xAxisExtraWidthRight = xAxisDimensions[1];
        int xAxisExtraHeightBottom = xAxisDimensions[2];
        if (xAxisExtraWidthLeft > extraWidthLeft) {
            int diff = xAxisExtraWidthLeft - dataStartCol;
            imageWidth += diff;
            dataStartCol += diff;
            dataEndCol += diff;
            extraWidthLeft = xAxisExtraWidthLeft;
            setOriginCol();
        }
        if (xAxisExtraWidthRight > extraWidthRight) {
            imageWidth += xAxisExtraWidthRight - extraWidthRight;
            extraWidthRight = xAxisExtraWidthRight;
        }
        xAxisHeight = xAxisExtraHeightBottom;
        if (xAxisExtraHeightBottom > extraHeightBottom) {
            imageHeight += xAxisExtraHeightBottom - extraHeightBottom;
            extraHeightBottom = xAxisExtraHeightBottom;
        }
    }

    public abstract int[] drawXAxis(int textHeight, int scaleTickLength,
            int scaleTickAndTextSeparation, int partTitleGap,
            int seperationDistanceOfAxisAndData);

    public abstract int[] drawYAxis(int interval, int textHeight,
            int startOfEndInterval, int scaleTickLength,
            int scaleTickAndTextSeparation, int partTitleGap,
            int seperationDistanceOfAxisAndData);

    public void drawOutline() {
        //Color color = g2.getColor();
        setPaint(Color.WHITE);
        Rectangle2D rect = new Rectangle2D.Double(0, 0, imageWidth, imageHeight);
        fillRect(0, 0, imageWidth, imageHeight);
        setPaint(Color.DARK_GRAY);
        draw(rect);
        //setPaint(color);
    }

    /**
     * The title is draw above the data area and centred on the whole width of
     * the image.
     *
     * @param title The title to be drawn.
     */
    public void drawTitle(String title) {
        setPaint(Color.BLACK);
        int oldExtraHeightTop = extraHeightTop;
        int textHeight = getTextHeight();
        extraHeightTop = textHeight * 3;
        int titleTextWidth = getTextWidth(title) + (textHeight * 2);
        if (titleTextWidth <= imageWidth) {
            int startPosition = (imageWidth / 2) - (titleTextWidth / 2) + textHeight;
            if (startPosition < 0) {
                startPosition = 0;
            }
            drawString(title, startPosition, textHeight * 2);
        }
        if (titleTextWidth > imageWidth) {
            int extrax = titleTextWidth - imageWidth;
            extraWidthRight = extrax / 2;
            int newExtraWidthLeft = extrax - extraWidthRight;
            if (newExtraWidthLeft > extraWidthLeft) {
                extraWidthLeft = newExtraWidthLeft;
                dataStartCol = extraWidthLeft;
                dataEndCol = dataStartCol + dataWidth;
                setOriginCol();
            }
            imageWidth = titleTextWidth;
        }
        if (extraHeightTop > oldExtraHeightTop) {
            int extraHeightDiff = extraHeightTop - oldExtraHeightTop;
            imageHeight += extraHeightDiff;
            dataStartRow = extraHeightTop;
            dataEndRow = dataStartRow + dataHeight;
            dataMiddleRow = ((dataEndRow - dataStartRow) / 2) + dataStartRow;
            setOriginRow();
        }
//        // Debug
//        System.out.println("imageHeight " + imageHeight);
    }

    public int getTextWidth(String text) {
        char[] c = text.toCharArray();
        int r = fontMetrics.charsWidth(c, 0, c.length);
        return r;
    }

    public int getTextHeight() {
        if (fontMetrics == null) {
            initFontMetrics();
        }
        return fontMetrics.getHeight();
    }

    public void writeText(String text, double angle, int startCol,
            int startRow) {
        // Store the current transform to return the graphics environment to
        AffineTransform currentTransform = null;
        if (g2 != null) {
            currentTransform = g2.getTransform();
        }
        AffineTransform newTransform = AffineTransform.getRotateInstance(
                angle, startCol, startRow);
        transform(newTransform);
        drawString(text, startCol, startRow);
        if (currentTransform != null) {
            setTransform(currentTransform);
        }

    }

    @Override
    public Dimension draw() {
        drawOutline();
        drawTitle(title);
        drawAxes(getAgeInterval(), getStartAgeOfEndYearInterval());
        drawData();
        Dimension newDim = new Dimension(imageWidth, imageHeight);
        return newDim;
    }

    public abstract void drawData();

    @Override
    public Dimension draw(Graphics2D g2) {
        this.g2 = g2;
        return draw();
    }

    /**
     *
     */
    @Override
    public void run() {
        try {
            Canvas = new Chart_Canvas();
            Canvas.Plot = this;
            Canvas.rect = new Rectangle(0, 0, getDataWidth(), getDataHeight());
            //            Graphics2D g2 = (Graphics2D) Canvas.getGraphics();
            //            setG2(g2);
            //            //Canvas.paint(g2);
            //            Dimension dim = draw();
            //            Canvas.setDimension(dim);
            PrinterJob pj = PrinterJob.getPrinterJob();
            Chart_Printable printable = new Chart_Printable(Canvas);
            pj.setPrintable(printable);
            String psMimeType = "application/postscript";
            FileOutputStream fos = null;
            //        PrintService[] printServices = PrinterJob.lookupPrintServices();
            StreamPrintService streamPrintService = null;
            StreamPrintServiceFactory[] spsf;
            spsf = PrinterJob.lookupStreamPrintServices(psMimeType);
            Path dir = file.getParent();
            Files.createDirectories(dir);
            Path psFile = Paths.get(dir.toString(), file.getFileName() + ".ps");
            System.out.println("psFile " + psFile.toString());
            if (spsf.length > 0) {
                try {
                    Files.createFile(psFile);
                    fos = new FileOutputStream(psFile.toFile());
                    streamPrintService = spsf[0].getPrintService(fos);
                    // streamPrintService can now be set as the service on a PrinterJob
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                    e.printStackTrace(System.err);
                }
            }
            try {
                //            PrintRequestAttributeSet pras;
                //            PrintService printService = printerJob.getPrintService();
                //            PrintServiceAttributeSet psas = printService.getAttributes();
                //            System.out.println(psas.toString());
                //            printerJob.print();
                //            System.out.println("pj.getJobName()" + printerJob.getJobName());
                //            System.out.println("pj.isCancelled()" + printerJob.isCancelled());
                //            printerJob.setPrintService(printServices[0]);
                pj.setPrintService(streamPrintService);
                PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
                pras.add(new Copies(1));
                pj.print(pras);
                setBi(Canvas.getBufferedImage());
                //            Rectangle r = Canvas.getBounds();
                //            Graphics2D g2 = (Graphics2D) Canvas.getGraphics();
                //            setG2(g2);
                //            //Canvas.paint(g2);
                //            Dimension dim = draw();
                //            Canvas.setDimension(dim);
                //Canvas.setDimension(Canvas.paintAndGetNewDimensions(g2));
                //Canvas.rect = new Rectangle(dim);
                //Canvas.setSize(dataWidth, dataHeight);
                // Canvas.paint(g2);
                long delay = 10000;
                Generic_Visualisation v = new Generic_Visualisation(env);
                future = v.saveImage(executorService, this, Canvas.getBufferedImage(), delay, format, file);
                //            fileOutputStream.close();
                //            printerJob.cancel();
                //            psFile.delete();
                ////            System.out.println("pj.getJobName() " + printerJob.getJobName());
                ////            System.out.println("pj.isCancelled() " + printerJob.isCancelled());
                //        } catch (IOException e) {
                //            System.err.println(e.getMessage());
                //            e.printStackTrace(System.err);
            } catch (PrinterException e) {
                e.printStackTrace(System.err);
            } finally {
                //printerJob.cancel();
                try {
                    fos.close();
                    pj.cancel();
                    Files.delete(psFile);
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                }
            }
        } catch (OutOfMemoryError e) {
            long time;
            //time = 60000L; // 1 minute
            //time = 120000L;// 2 minutes
            time = 240000L; // 4 minutes
            Generic_Execution.waitSychronized(env, this, time); // wait a time
            System.out.println("...on we go.");
            run();
        } catch (IOException ex) {
            Logger.getLogger(Chart.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
