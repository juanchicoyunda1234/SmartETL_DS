package etl;

public final class LectorCSV {

    private LectorCSV() {
    }

    public static String[] separar(String linea) {
        String[] campos = new String[contarCampos(linea)];
        StringBuilder actual = new StringBuilder();
        boolean enComillas = false;
        int indice = 0;
        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
            if (enComillas) {
                if (c == '"') {
                    if (i + 1 < linea.length() && linea.charAt(i + 1) == '"') {
                        actual.append('"');
                        i++;
                    } else {
                        enComillas = false;
                    }
                } else {
                    actual.append(c);
                }
            } else if (c == '"') {
                enComillas = true;
            } else if (c == ',') {
                campos[indice] = actual.toString();
                indice++;
                actual.setLength(0);
            } else {
                actual.append(c);
            }
        }
        campos[indice] = actual.toString();
        return campos;
    }

    private static int contarCampos(String linea) {
        int separadores = 0;
        boolean enComillas = false;
        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
            if (c == '"') {
                enComillas = !enComillas;
            } else if (c == ',' && !enComillas) {
                separadores++;
            }
        }
        return separadores + 1;
    }
}
