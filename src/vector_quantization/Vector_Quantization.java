package vector_quantization;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;
import javax.imageio.ImageIO;

/**
 *
 * @author HDrmi
 */
public class Vector_Quantization {

    private static List<Double[][]> blocks;
    private static Map<Integer, int[][]> codebook;

    public Vector_Quantization() {
        blocks = new ArrayList<>();
        codebook = new HashMap<>();
    }

    private void View(int[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                System.out.print(arr[i][j] + " ");
            }
            System.out.println(" ");
        }
    }

    private List<Double[][]> SplitPixelsArray(int[][] pixles, int ro, int co) {
        List<Double[][]> list = new ArrayList<>();
        for (int row = 0; row < (pixles.length); row += ro) {
            for (int col = 0; col < (pixles[0].length); col += co) {
                Double[][] subDoubles = new Double[ro][co];
                for (int i = 0; i < ro; i++) {
                    for (int j = 0; j < co; j++) {
                        subDoubles[i][j] = (double) pixles[i + row][j + col];
                    }
                }
                list.add(subDoubles);
            }
        }
        return list;
    }

    private int MSE(Double[][] a, Double[][] b) {
        int retvalue = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                retvalue += ((a[i][j] - b[i][j]) * (a[i][j] - b[i][j]));
            }
        }
        return retvalue;
    }

    private int MSE(int[][] a, int[][] b) {
        int retvalue = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                retvalue += ((a[i][j] - b[i][j]) * (a[i][j] - b[i][j]));
            }
        }
        return retvalue;
    }

    private Double[][] Lessone(Double[][] avg) {
        Double[][] newDoubleses = new Double[avg.length][avg[0].length];
        for (int i = 0; i < avg.length; i++) {
            for (int j = 0; j < avg[0].length; j++) {
                if (avg[i][j] == avg[i][j].intValue()) {
                    newDoubleses[i][j] = avg[i][j] - 1;
                } else {
                    newDoubleses[i][j] = (double) avg[i][j].intValue();
                }
            }
        }
        return newDoubleses;
    }

    private Double[][] Greaterone(Double[][] avg) {
        Double[][] newDoubleses = new Double[avg.length][avg[0].length];
        for (int i = 0; i < avg.length; i++) {
            for (int j = 0; j < avg[0].length; j++) {
                if (avg[i][j] == avg[i][j].intValue()) {
                    newDoubleses[i][j] = avg[i][j] + 1;
                } else {
                    newDoubleses[i][j] = (double) avg[i][j].intValue() + 1;
                }
            }
        }
        return newDoubleses;
    }

    private List<VectorNode> SpiltAndAssociate(List<VectorNode> Nodes, int level) {
        List<VectorNode> temp = null;
        while (Nodes.size() != level) {
            temp = new ArrayList<>();
            for (VectorNode node : Nodes) {
                Double[][] averageless = Lessone(node.getAverage());
                Double[][] averagegreater = Greaterone(node.getAverage());
                VectorNode n1 = new VectorNode();
                n1.setAverage(averageless);
                VectorNode n2 = new VectorNode();
                n2.setAverage(averagegreater);
                temp.add(n1);
                temp.add(n2);
            }
            for (Double[][] arr : blocks) {
                int i = 0, loc = 0;
                int min = Integer.MAX_VALUE;
                for (VectorNode node : temp) {
                    int error = MSE(node.getAverage(), arr);
                    if (error < min) {
                        min = error;
                        loc = i;
                    }
                    i++;
                }
                temp.get(loc).AddToList(arr);
            }
            for (VectorNode node : temp) {
                node.CalculateAvg();
            }
            Nodes = new ArrayList<>(temp);
        }
        return temp;
    }

    private int[][] readImage(String filePath) {
        int width = 0;
        int height = 0;
        File file = new File(filePath);
        BufferedImage image = null;
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
        }
        width = image.getWidth();
        height = image.getHeight();
        int[][] pixels = new int[height][width];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = image.getRGB(x, y);
                int alpha = (rgb >> 24) & 0xff;
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = (rgb) & 0xff;
                pixels[y][x] = r;
            }
        }
        return pixels;
    }

    private void writeImage(int[][] pixels, String outputFilePath, int width, int height) {
        File fileout = new File(outputFilePath);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, (pixels[y][x] << 16) | (pixels[y][x] << 8) | (pixels[y][x]));
            }
        }
        try {
            ImageIO.write(image, "jpg", fileout);
        } catch (IOException e) {
        }
    }

    private boolean Compare(VectorNode a, VectorNode b) {
        if (a.getAverage() != b.getAverage()) {
            return false;
        } else if (!a.getList().equals(b.getList())) {
            return false;
        }
        return true;
    }

    private void Associate(List<VectorNode> Nodes) {
        int it = 100;
        boolean flag = true;
        while (it-- != 0 && flag) {
            List<VectorNode> temp = new ArrayList<>();
            for (VectorNode node : Nodes) {
                VectorNode n = new VectorNode();
                n.setAverage(node.getAverage());
                temp.add(n);
            }
            for (Double[][] arr : blocks) {
                int i = 0, loc = 0;
                int min = Integer.MAX_VALUE;
                for (VectorNode node : temp) {
                    int error = MSE(node.getAverage(), arr);
                    if (error < min) {
                        min = error;
                        loc = i;
                    }
                    i++;
                }
                temp.get(loc).AddToList(arr);
            }
            for (VectorNode node : temp) {
                node.CalculateAvg();
            }
            boolean ch = false;
            for (int i = 0; i < Nodes.size(); i++) {
                if (!Compare(Nodes.get(i), temp.get(i))) {
                    ch = true;
                    break;
                }
            }
            if (!ch) {
                flag = false;
            } else {
                Nodes = new ArrayList<>(temp);
            }
        }
    }

    private void mapping(List<VectorNode> list) {
        codebook = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            Double[][] nar = list.get(i).getAverage();
            int[][] newar = new int[nar.length][nar[0].length];
            for (int x = 0; x < nar.length; x++) {
                for (int y = 0; y < nar[0].length; y++) {
                    newar[x][y] = nar[x][y].intValue();
                }
            }
            codebook.put(i, newar);
        }
    }

    private int[][] replace(Map<Integer, int[][]> map, int[][] pixles, int ro, int co, int w, int h) {
        int[][] array = new int[w][h];
        for (int row = 0, arwo = 0; row < (pixles.length); row += ro, arwo++) {
            for (int col = 0, arco = 0; col < (pixles[0].length); col += co, arco++) {
                int[][] subIntegers = new int[ro][co];
                for (int i = 0; i < ro; i++) {
                    for (int j = 0; j < co; j++) {
                        subIntegers[i][j] = pixles[i + row][j + col];
                    }
                }
                int loc = 0;
                int min = Integer.MAX_VALUE;
                for (Map.Entry<Integer, int[][]> node : map.entrySet()) {
                    int error = MSE(subIntegers, node.getValue());
                    if (error < min) {
                        min = error;
                        loc = node.getKey();
                    }
                }
                array[arwo][arco] = loc;
            }
        }
        return array;
    }

    private byte[] Read() {
        byte[] data = null;
        try {
            Path path = Paths.get("compress.txt");
            data = Files.readAllBytes(path);
        } catch (IOException ex) {
            Logger.getLogger(Vector_Quantization.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }

    private void WriteInFile(Map<Integer, int[][]> map, int[][] pixles, int vw, int vh) {
        try {
            try (DataOutputStream out = new DataOutputStream(new FileOutputStream("compress.txt"))) {
                int size = map.size();
                out.writeByte(vw);
                out.writeByte(vh);
                out.writeByte(size);
                for (Map.Entry<Integer, int[][]> entry : map.entrySet()) {
                    int[][] arr = entry.getValue();
                    for (int i = 0; i < arr.length; i++) {
                        for (int j = 0; j < arr[0].length; j++) {
                            out.writeByte(arr[i][j]);
                        }
                    }
                }
                int pixw = pixles.length, pixh = pixles[0].length;
                out.writeInt(pixw);
                out.writeInt(pixh);
                for (int i = 0; i < pixw; i++) {
                    for (int j = 0; j < pixh; j++) {
                        out.writeByte(pixles[i][j]);
                    }
                }
                out.close();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Vector_Quantization.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Vector_Quantization.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Compress(int vw,int vh,int codebooksi) {
        codebook.clear();
        int vecw = vw, vech = vh;
        int codebooksize = codebooksi;
        int[][] pixels = readImage("C:\\Users\\HDrmi\\Documents\\NetBeansProjects\\Vector_Quantization\\lena.jpg");
        blocks = SplitPixelsArray(pixels, vecw, vech);
        VectorNode node = new VectorNode(blocks);
        node.CalculateAvg();
        List<VectorNode> nodes = new ArrayList<>();
        nodes.add(node);
        List<VectorNode> nos = new ArrayList<>(SpiltAndAssociate(nodes, codebooksize));
        Associate(nos);
        mapping(nos);
        int[][] compress = replace(codebook, pixels, vecw, vech, pixels.length / vecw, pixels[0].length / vech);
        WriteInFile(codebook, compress, vecw, vech);
    }

    public void Decompress(String Name) {
        codebook.clear();
        byte[] data = Read();
        int i = 0;
        int vw = Byte.toUnsignedInt(data[i++]);
        int vh = Byte.toUnsignedInt(data[i++]);
        int size = Byte.toUnsignedInt(data[i++]);
        for (int j = 0; j < size; j++) {
            int[][] temp = new int[vw][vh];
            for (int ro = 0; ro < vw; ro++) {
                for (int co = 0; co < vh; co++) {
                    temp[ro][co] = Byte.toUnsignedInt(data[i++]);
                }
            }
            codebook.put(j, temp);
        }
        int pixw = (Byte.toUnsignedInt(data[i++])<<24) | (Byte.toUnsignedInt(data[i++])<<16)|
                (Byte.toUnsignedInt(data[i++])<<8)|Byte.toUnsignedInt(data[i++]);
        int pixh =(Byte.toUnsignedInt(data[i++])<<24) | (Byte.toUnsignedInt(data[i++])<<16)|
                (Byte.toUnsignedInt(data[i++])<<8)|Byte.toUnsignedInt(data[i++]);
        System.out.println(pixw+" "+pixh);
        List<Integer> list = new ArrayList<>();
        int[][] pixles = new int[pixw][pixh];
        for (int ro = 0; ro < pixw; ro++) {
            for (int co = 0; co < pixh; co++) {
                pixles[ro][co] = Byte.toUnsignedInt(data[i++]);
                list.add(pixles[ro][co]);
            }
        }
        int[][] compressed = new int[pixw * vw][pixh * vh];
        int r = 0;
        for (int row = 0; row < (compressed.length); row += vw) {
            for (int col = 0; col < (compressed[0].length); col += vh) {
                int[][] subDoubles = codebook.get(list.get(r++));
                for (int x = 0; x < vw; x++) {
                    for (int y = 0; y < vh; y++) {
                        compressed[x + row][y + col] = subDoubles[x][y];
                    }
                }
            }
        }
        writeImage(compressed, "C:\\Users\\HDrmi\\Documents\\NetBeansProjects\\Vector_Quantization\\" + Name + ".jpg", compressed.length, compressed[0].length
        );
    }

}
