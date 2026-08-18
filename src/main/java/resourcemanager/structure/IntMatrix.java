package resourcemanager.structure;

public class IntMatrix {
    private int[][] matrix;
    private int rows; // filas
    private int columns; // columnas
    private int usedFields; // cantidad de espacios ocupados
    public IntMatrix(int rows, int columns){
        // inicializar todos los campos con 0
        matrix = new int[rows][columns];
        for(int row=0;row<rows;row++){
            for(int column=0;column<columns;column++){
                matrix[row][column] = 0;
            }
        }
        this.rows=rows;
        this.columns=columns;
        usedFields = 0;
    }

    public void insert(int iRow, int iColumn, int value){
        if(iRow>rows-1||iColumn>columns-1){
            throw new ArrayIndexOutOfBoundsException("El indice especificado para la matriz de enteros esta fuera del rango");
        }
        matrix[iRow][iColumn] = value;
        usedFields++;
    }

    public int get(int iRow, int iColumn){
        if(iRow>rows-1||iColumn>columns-1){
            throw new ArrayIndexOutOfBoundsException("El indice especificado para la matriz de enteros esta fuera del rango");
        }
        return matrix[iRow][iColumn];
    }

    public int size() {return rows*columns;}
}
