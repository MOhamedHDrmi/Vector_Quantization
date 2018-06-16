package vector_quantization;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author HDrmi
 */
public class VectorNode {

    private List<Double[][]> list;
    private Double[][] Average;

    public VectorNode() {
        this.list = new ArrayList<>();
    }

    public VectorNode(List<Double[][]> list, Double[][] Average) {
        this.list = new ArrayList<>(list);
        this.Average = Average;

    }

    public VectorNode(List<Double[][]> list) {
        this.list = new ArrayList<>(list);
    }
    
    public void AddToList(Double[][] arr) {
        this.list.add(arr);
    }

    public void CalculateAvg() {
        this.Average = new Double[this.list.get(0).length][this.list.get(0)[0].length];
        for (int i = 0; i < this.Average.length; i++) {
            for (int j = 0; j < this.Average[0].length; j++) {
                this.Average[i][j] = 0.0;
            }
        }
        for (Double[][] arr : this.list) {
            for (int i = 0; i < arr.length; i++) {
                for (int j = 0; j < arr[0].length; j++) {
                    this.Average[i][j] += arr[i][j];
                }
            }
        }
        for (int i = 0; i < this.Average.length; i++) {
            for (int j = 0; j < this.Average[0].length; j++) {
                this.Average[i][j] = this.Average[i][j] / list.size();
            }
        }
    }

    public List<Double[][]> getList() {
        return list;
    }

    public Double[][] getAverage() {
        return Average;
    }

    public void setAverage(Double[][] Average) {
        this.Average = Average;
    }

}
