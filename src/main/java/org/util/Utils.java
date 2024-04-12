package org.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utils {

    private Utils(){}

    public static final Random random = new Random();
    public static final DecimalFormat dF = new DecimalFormat("#.##");

    /**
     * Method to initialize input data.
     * @return input Buffered string data.
     */
    public static String init() {
        String buffer = "";
        InputStreamReader stream = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(stream);
        try {
            buffer = reader.readLine();
        } catch (Exception e) {
            System.out.append("Dato no válido.");
        }
        return buffer;
    }

    /**
     * Method to get an integer from input data.
     * @return integer entered by the user.
     */
    public static int integer() {
        return Utils.integer(null);
    }

    /**
     * Method to get an integer from input data with a custom message.
     * @param message custom message for the user.
     * @return integer entered by the user.
     */
    public static int integer(String message) {
        if (message != null) {
            System.out.print(message);
        }

        try{
            return Integer.parseInt(Utils.init());
        } catch (NumberFormatException e){
            System.out.println("El valor no es un número entero.");
            return integer(message);
        }
    }


    /**
     * Method to get a real number from input data.
     * @return real number entered by the user.
     */
    public static double real() {
        return Utils.real(null);
    }

    /**
     * Method to get a real number from input data with a custom message.
     * @param message custom message for the user.
     * @return real number entered by the user.
     */
    public static double real(String message) {
        if (message != null) {
            System.out.print(message);
        }
        try{

            return Double.parseDouble(Utils.init());
        } catch (NumberFormatException e){
            System.out.println("El valor no es un número real.");
            return real(message);
        }
    }

    /**
     * Method to get a text string from input data.
     * @return text string entered by the user.
     */
    public static String string() {
        return Utils.string(null);
    }


    /**
     * Method to get a text string from input data with a custom message.
     * @param message custom message for the user.
     * @return text string entered by the user.
     */
    public static String string(String message) {
        if (message != null) {
            System.out.print(message);
        }
        return Utils.init();
    }

    /**
     * Method to get a character from input data.
     * @return character entered by the user.
     */
    public static char character() {
        return Utils.character(null);
    }

    /**
     * Method to get a character from input data with a custom message.
     * @param message custom message for the user.
     * @return character entered by the user.
     */
    public static char character(String message) {
        if (message != null) {
            System.out.print(message);
        }
        String valor = Utils.init();
        return valor.charAt(0);
    }

    /**
     * Formats the {@link Double} value passed as a parameter with the pattern #.##
     *
     * @param d
     *            the value to be transformed
     * @return The formatted double
     */
    public static String formatLocalNumber(double d) {
        return dF.format(d);
    }

    /**
     * Retrieves a random number selected from the values passed as parameters.
     *
     * @param min
     *            The lower limit of the range
     * @param max
     *            The upper limit of the range
     * @return A random number within the selected range.
     */
    public static int getRandomNumberInRange(int min, int max) {
        max++;
        return random.nextInt(max - min) + min;
    }

    /**
     * Displays a list with indices and allows it to show a waiting message.
     *
     * @param list
     *            The list to be displayed
     * @param wait
     *            <code>true</code> to wait after displaying the list,
     *            <code>false</code> otherwise.
     */
    public static <T> void showFromList(List<T> list, boolean wait) {
        Utils.showFromList(list, wait, null);
    }

    /**
     * Displays a list with indices, allows showing a waiting message, and excludes
     * the elements from the first list that are present in the second list.
     *
     * @param list
     *            The list to be displayed
     * @param wait
     *            <code>true</code> to wait after displaying the list,
     *            <code>false</code> otherwise.
     * @param excludeElements
     *            Excludes the elements that exist in this list from the list
     *            passed as a parameter.
     */
    public static <T> void showFromList(List<T> list, boolean wait, List<T> excludeElements) {
        StringBuilder builder = new StringBuilder();
        List<Object> auxList = new ArrayList<>();
        auxList.addAll(list);
        if (excludeElements != null) {
            auxList.removeAll(excludeElements);
        }
        for (int i = 0; i < auxList.size(); i++) {
            builder.append("\t");
            builder.append(i + 1);
            builder.append(". ");
            builder.append(auxList.get(i).toString());
            builder.append("\n");
        }
        System.out.print(builder.toString());
        if (wait) {
            Utils.string("\nPulse \"Enter\" para continuar...");
        }
    }

    /**
     * Returns a text string as a representation of a list, optionally numbered.
     *
     * @param list           The list of elements to be displayed in the string representation.
     * @param id             A boolean flag that determines whether the elements should be numbered.
     * @param <T>            The type of elements contained in the list.
     * @return A string representing the list elements, optionally numbered.
     */
    public static <T> String returnShowFromList(List<T> list, boolean id) {
        return Utils.returnShowFromList(list, id, null);
    }

    /**
     * Returns a text string as a representation of a list, optionally numbered,
     * excluding specified elements in another list.
     *
     * @param list           The list of elements to be displayed in the string representation.
     * @param id             A boolean flag that determines whether the elements should be numbered.
     * @param excludeElements A list of elements to be excluded from the string representation,
     *                       can be null if no elements are to be excluded.
     * @param <T>            The type of elements contained in the list.
     * @return A string representing the list elements, optionally numbered, and excluding
     *         the elements specified in {@code excludeElements}.
     */
    public static <T> String returnShowFromList(List<T> list, boolean id, List<T> excludeElements) {
        StringBuilder builder = new StringBuilder();
        List<Object> auxList = new ArrayList<>();
        auxList.addAll(list);
        if (excludeElements != null) {
            auxList.removeAll(excludeElements);
        }
        for (int i = 0; i < auxList.size(); i++) {
            builder.append("\t");
            if (id) {
                builder.append(i + 1);
                builder.append(". ");
            }
            builder.append(auxList.get(i).toString());
            builder.append("\n");
        }
        return builder.toString();
    }

    /**
     * Returns a list of one element with the selected option from the list,
     * with the option to cancel that selection.
     *
     * @param list
     *            The list from which a selection will be made.
     * @param cancel
     *            <code>true</code> to allow canceling the selection,
     *            <code>false</code> otherwise.
     * @return An array of one element with the position occupied by the selected element in the list.
     */
    public static <T> List<T> showAndSelectFromList(List<T> list, boolean cancel) {
        return Utils.showAndSelectFromList(list, cancel, false);
    }

    /**
     * Returns a list of multiple elements with the selected option from the list,
     * with the option to cancel that selection, or to return an array with the positions of the selections.
     *
     * @param list
     *            The list from which selections will be made.
     * @param cancel
     *            <code>true</code> to allow canceling the selection,
     *            <code>false</code> otherwise.
     * @param multipleReturn
     *            <code>true</code> to allow the array to contain multiple selections,
     *            <code>false</code> otherwise.
     * @return A list with one or multiple selected elements from the list.
     */
    public static <T> List<T> showAndSelectFromList(List<T> list, boolean cancel, boolean multipleReturn) {
        return Utils.showAndSelectFromList(list, cancel, multipleReturn, null);
    }

    /**
     * Returns a list of multiple elements with the selected option from the list,
     * with the option to cancel that selection, or to return an array with the positions of the selections.
     * Elements from the second list can be excluded.
     *
     * @param list
     *            The list from which selections will be made.
     * @param cancel
     *            <code>true</code> to allow canceling the selection,
     *            <code>false</code> otherwise.
     * @param multipleReturn
     *            <code>true</code> to allow the array to contain multiple selections,
     *            <code>false</code> otherwise.
     * @param excludeElements
     *            Elements from the list to be excluded.
     * @return A list with one or multiple selected elements from the list.
     */

    public static <T> List<T> showAndSelectFromList(List<T> list, boolean cancel, boolean multipleReturn, List<T> excludeElements) {
        Utils.showFromList(list, false, excludeElements);
        List<T> auxList = new ArrayList<>(list);
        List<T> toRet = new ArrayList<>();
        if (excludeElements != null) {
            auxList.removeAll(excludeElements);
        }
        StringBuilder builder = new StringBuilder();
        if (!multipleReturn) {
            builder.append("\nSeleccione el elemento deseado");
            if (cancel) {
                builder.append(", 0 para salir");
            }
            builder.append(": ");
            int selected = Utils.integer(builder.toString());
            while (!Utils.checkSelection(selected, list.size()) && (selected != 0)) {
                selected = Utils.integer("La opción no es válida, por favor, escoje una opción válida: ");
            }

            if (selected == 0) {
                return toRet;
            }

            toRet.add(auxList.get(selected - 1));
            return toRet;

        } else {
            builder.append("\nSeleccione los elementos deseados, separándolos por , ");
            if (cancel) {
                builder.append("(0 para salir)");
            }
            builder.append(": ");
            String auxSelected = Utils.string(builder.toString());
            auxSelected = auxSelected.replace(" ", "");
            String[] split = auxSelected.split(",");
            int[] selectionIndex = Utils.parseIntArray(split);
            if (selectionIndex[0]<0){
                return toRet;
            }

            for (int i = 0; i<selectionIndex.length; i++){
                toRet.add(auxList.get(selectionIndex[i]));
            }
            return toRet;
        }
    }

    /**
     * Converts all elements in the array of type String and returns the
     * array with the elements transformed into elements of type Integer.
     *
     * @param arr
     *            an array of elements of type {@link String}
     * @return an array of elements of type {@link Integer}
     */
    static int[] parseIntArray(String[] arr) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            list.add(Integer.valueOf(arr[i]) - 1);
        }
        return list.stream().mapToInt(i -> i).toArray();
    }

    /**
     * Checks if the index passed as the first parameter is within the bounds of a
     * list of parameters. Returns <code>true</code> if the index is greater than or
     * equal to 0 and less than or equal to the size of the list, <code>false</code>
     * otherwise.
     *
     * @param i
     *            The index passed as a parameter
     * @param size
     *            The size of the list of elements
     * @return <code>true</code> if the index is greater than or equal to 0 and less
     *         than or equal to the size of the list, <code>false</code> otherwise
     */
    public static boolean checkSelection(int i, int size) {
        return (i >= 1) && (i <= size);
    }
}