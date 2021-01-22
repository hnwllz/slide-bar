package rest;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
 
import javax.imageio.ImageIO;
import rest.Base64Utils;;
 
 
 
public class VerifyImageUtil {
 
//    private static Logger log = LoggerFactory.getLogger(VerifyImageUtil.class);
    private static int BOLD = 5;
    private static final String IMG_FILE_TYPE = "jpg";
    private static final String TEMP_IMG_FILE_TYPE = "png";
 
    /**
     * ����ģ����ͼ
     * @param templateFile
     * @param targetFile
     * @return
     * @throws Exception
     */
    public static Map<String, Object> pictureTemplatesCut(File templateFile, File targetFile) throws Exception {
        Map<String, Object> pictureMap = new HashMap<String, Object>();
        // ģ��ͼ
        BufferedImage imageTemplate = ImageIO.read(templateFile);
        int templateWidth = imageTemplate.getWidth();
        int templateHeight = imageTemplate.getHeight();
 
        // ԭͼ
        BufferedImage oriImage = ImageIO.read(targetFile);
        int oriImageWidth = oriImage.getWidth();
        int oriImageHeight = oriImage.getHeight();
 
        //������ɿ�ͼ����X,Y
        //X������Ҷ�targetWidth  Y�����ײ�targetHeight����
        Random random = new Random();
        int widthRandom = random.nextInt(oriImageWidth - 2*templateWidth) + templateWidth;
        int heightRandom = random.nextInt(oriImageHeight - templateHeight);
//        log.info("ԭͼ��С{} x {},������ɵ����� X,Y Ϊ��{}��{}��", oriImageWidth,oriImageHeight,widthRandom,heightRandom);
 
        // �½�һ����ģ��һ����С��ͼ��TYPE_4BYTE_ABGR��ʾ����8λRGBA��ɫ������ͼ������ȡimageTemplate.getType()
//        BufferedImage newImage = new BufferedImage(templateWidth, templateHeight, imageTemplate.getType());
        BufferedImage newImage = new BufferedImage(templateWidth, templateHeight, BufferedImage.TYPE_4BYTE_ABGR);
        //�õ����ʶ���
        Graphics2D graphics = newImage.createGraphics();
        //�����Ҫ����RGB��ʽ����Ҫ����������,Transparency ����͸��
        newImage = graphics.getDeviceConfiguration().createCompatibleImage(templateWidth, templateHeight, Transparency.TRANSLUCENT);
 
        // �½���ͼ�����ģ����ɫ��ֵ,Դͼ��������
        cutByTemplate(oriImage,imageTemplate,newImage,widthRandom,heightRandom);
 
        // ���á�����ݡ�������
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setStroke(new BasicStroke(BOLD, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        graphics.drawImage(newImage, 0, 0, null);
        graphics.dispose();
 
        ByteArrayOutputStream newImageOs = new ByteArrayOutputStream();//�½�����
        ImageIO.write(newImage, TEMP_IMG_FILE_TYPE, newImageOs);//����ImageIO���ṩ��write��������bi��pngͼƬ������ģʽд������
        byte[] newImagebyte = newImageOs.toByteArray();
 
        ByteArrayOutputStream oriImagesOs = new ByteArrayOutputStream();//�½�����
        ImageIO.write(oriImage, IMG_FILE_TYPE, oriImagesOs);//����ImageIO���ṩ��write��������bi��jpgͼƬ������ģʽд������
        byte[] oriImageByte = oriImagesOs.toByteArray();
 
        pictureMap.put("smallImage", Base64Utils.encodeToString(newImagebyte));
        pictureMap.put("bigImage", Base64Utils.encodeToString(oriImageByte));
        System.out.println("widthRandom:"+widthRandom);
        pictureMap.put("xWidth",widthRandom);
        pictureMap.put("yHeight",heightRandom);
        return pictureMap;
    }
 
    /**
     * ���ˮӡ
     * @param oriImage
     */
    /*private static BufferedImage addWatermark(BufferedImage oriImage) throws IOException {
        Graphics2D graphics2D = oriImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        // ����ˮӡ������ɫ
        graphics2D.setColor(Color.BLUE);
        // ����ˮӡ����Font
        graphics2D.setFont(new java.awt.Font("����", java.awt.Font.BOLD, 50));
        // ����ˮӡ����͸����
        graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f));
        // ��һ����->���õ����ݣ�������������->������ͼƬ�ϵ�����λ��(x,y)
        graphics2D.drawString("zhoujin@qq.com", 400,300);
        graphics2D.dispose(); //�ͷ�
        return oriImage;
    }*/
 
    /**
     * @param oriImage  ԭͼ
     * @param templateImage  ģ��ͼ
     * @param newImage  �¿ٳ���Сͼ
     * @param x         �����ȡ����X
     * @param y         �����ȡ����y
     * @throws Exception
     */
    private static void cutByTemplate(BufferedImage oriImage, BufferedImage templateImage,BufferedImage newImage, int x, int y){
        //��ʱ����������ڸ�˹ģ�����ܱ�����ֵ
        int[][] martrix = new int[3][3];
        int[] values = new int[9];
 
        int xLength = templateImage.getWidth();
        int yLength = templateImage.getHeight();
        // ģ��ͼ����
        for (int i = 0; i < xLength; i++) {
            // ģ��ͼƬ�߶�
            for (int j = 0; j < yLength; j++) {
                // ���ģ��ͼ��ǰ���ص㲻��͸��ɫ copyԴ�ļ���Ϣ��Ŀ��ͼƬ��
                int rgb = templateImage.getRGB(i, j);
                if (rgb < 0) {
                    newImage.setRGB(i, j,oriImage.getRGB(x + i, y + j));
 
                    //��ͼ�����˹ģ��
                    readPixel(oriImage, x + i, y + j, values);
                    fillMatrix(martrix, values);
                    oriImage.setRGB(x + i, y + j, avgMatrix(martrix));
                }
 
                //��ֹ����Խ���ж�
                if(i == (xLength-1) || j == (yLength-1)){
                    continue;
                }
                int rightRgb = templateImage.getRGB(i + 1, j);
                int downRgb = templateImage.getRGB(i, j + 1);
                //��ߴ���,ȡ�����غ������صĽ�㣬�жϸõ��ǲ����ٽ�������,��������ø����������ǰ�ɫ
                if((rgb >= 0 && rightRgb < 0) || (rgb < 0 && rightRgb >= 0) || (rgb >= 0 && downRgb < 0) || (rgb < 0 && downRgb >= 0)){
                    newImage.setRGB(i, j, Color.white.getRGB());
                    oriImage.setRGB(x + i, y + j,Color.white.getRGB());
                }
            }
        }
    }
 
    private static void readPixel(BufferedImage img, int x, int y, int[] pixels) {
        int xStart = x - 1;
        int yStart = y - 1;
        int current = 0;
        for (int i = xStart; i < 3 + xStart; i++)
            for (int j = yStart; j < 3 + yStart; j++) {
                int tx = i;
                if (tx < 0) {
                    tx = -tx;
 
                } else if (tx >= img.getWidth()) {
                    tx = x;
                }
                int ty = j;
                if (ty < 0) {
                    ty = -ty;
                } else if (ty >= img.getHeight()) {
                    ty = y;
                }
                pixels[current++] = img.getRGB(tx, ty);
 
            }
    }
 
    private static void fillMatrix(int[][] matrix, int[] values) {
        int filled = 0;
        for (int i = 0; i < matrix.length; i++) {
            int[] x = matrix[i];
            for (int j = 0; j < x.length; j++) {
                x[j] = values[filled++];
            }
        }
    }
 
    private static int avgMatrix(int[][] matrix) {
        int r = 0;
        int g = 0;
        int b = 0;
        for (int i = 0; i < matrix.length; i++) {
            int[] x = matrix[i];
            for (int j = 0; j < x.length; j++) {
                if (j == 1) {
                    continue;
                }
                Color c = new Color(x[j]);
                r += c.getRed();
                g += c.getGreen();
                b += c.getBlue();
            }
        }
        return new Color(r / 8, g / 8, b / 8).getRGB();
    }
}