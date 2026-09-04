package resourcemanager.logic;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import java.awt.Desktop;
import java.io.File;
import java.lang.reflect.Field;
import java.util.List;


import java.lang.reflect.Field;
import java.util.List;

public class PrintLogic {
    // generar pdf de forma genérica
    public <T> File generatePdf(List<T> items, Class<T> classType, String fileName) throws Exception {

        // generar nuevo documento PDF
        PDDocument document = new PDDocument();


        // obtener cuales atributos deben de ir para la clase
        Field[] fields = classType.getDeclaredFields();
        for (Field f : fields) f.setAccessible(true);

        // calcular anchura de columnas para que dependiendo de la cantidad de columnas sea mas apretado
        float margin = 50;
        float idealColWidth = 150;
        float totalWidth = fields.length * idealColWidth;

        PDRectangle pageSize = PDRectangle.A4;

        // calcular si se debe poner en horizontal, si son muchas columnas
        boolean landscape = totalWidth + (2 * margin) > pageSize.getWidth();
        if (landscape) {
            pageSize = new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth());
        }



        // crear hoja y agregarla al documento con el tamaño calculado
        PDPage page = new PDPage(pageSize);
        document.addPage(page);

        // poner dimensiones
        PDPageContentStream content = new PDPageContentStream(document, page);
        float yStart = pageSize.getHeight() - margin; // relativo porque si es en horizontal tiene que variar
        float rowHeight = 20;
        float y = yStart;
        float usableWidth = pageSize.getWidth() - (2 * margin);
        float colWidth = Math.min(idealColWidth, usableWidth / fields.length); // si hay demasiadas columnas, hacerlas mas pequeñas
        float fontSize = colWidth < 80 ? 8 : 11; // si las columnas estan demasiado apretadas entonces hacer texto mas pequeño

        // setear formato para encabezados
        PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        content.setFont(font, fontSize);

        // preparar para  empezar a escribir
        content.beginText();
        content.newLineAtOffset(margin, y);

        // para cada campo mostrar el encabezado siendo este el nombre del atributo y luego mover el cursor al siguiente espacio
        for (Field f : fields) {
            content.showText(f.getName());
            content.newLineAtOffset(colWidth, 0);
        }
        content.endText();
        y -= rowHeight;

        // ahora escribir cada fila, los valores para cada una de las columnas
        PDType1Font regularFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        content.setFont(regularFont, fontSize);

        for (T item : items) {
            // para cada item formatear en tabla
            content.beginText();
            content.newLineAtOffset(margin, y);
            // agregar cada valor donde corresponde para la fila en cuestion
            for (Field f : fields) {
                Object value = f.get(item);
                String text = value != null ? value.toString() : "";
                if (text.length() > 20) text = text.substring(0, 17) + "..."; // para evitar que se salga
                content.showText(text);
                content.newLineAtOffset(colWidth, 0);
            }
            content.endText();
            y -= rowHeight;

            if (y < margin) { // si se queda sin espacio hacer una nueva pagina
                content.close();
                page = new PDPage(pageSize);
                document.addPage(page);
                content = new PDPageContentStream(document, page);
                y = yStart;
            }
        }

        content.close();

        // crear archivo y guardarlo, luego retornarlo
        File file = new File(fileName);
        document.save(file);
        document.close();
        return file;
    }

    public void openPdf(File file) throws Exception {
        // si se puede abrir, entonces abrirlo con el visualizador predeterminado del sistema
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(file);
        }
    }
}
