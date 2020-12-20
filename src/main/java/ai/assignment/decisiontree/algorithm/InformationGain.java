package ai.assignment.decisiontree.algorithm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import ai.assignment.decisiontree.entity.InformationGainNode;
import ai.assignment.decisiontree.math.Calculate;
import ai.assignment.fileparser.IFParse;


public class InformationGain {
    
    private Calculate calculate = new Calculate();

    private InformationGainNode informationGainNode;
    private String[][] userTempData; 
    private String[][] userInput; 
    private int[] numberOfAttributeTypes; 

    public InformationGain() {
        informationGainNode = null;
        userTempData = null;
        userInput = null;
        numberOfAttributeTypes = null;
    } 

    public void printTree() {

        String convertTreeToString = informationGainNode.convertToString("", userTempData, userInput);
        System.out.println(convertTreeToString);
    } 


    public void generateDecisionTree() throws FileNotFoundException, IOException {

        String[][] userTempInput = new IFParse().getUserInput();

        convertArray(userTempInput);
        
        informationGainNode = generateDecisionTree(new ArrayList<Integer>(), new ArrayList<Integer>());
        
    }
    

    private InformationGainNode generateDecisionTree(ArrayList<Integer> ignoredCols, ArrayList<Integer> notIncludedRows) {
        if (notIncludedRows.size() == userTempData.length - 1) {
            return null;
        }
        if (ignoredCols.size() >= userTempData[0].length - 1) {
            return new InformationGainNode(null, getMostCommonAttribute(notIncludedRows));
        }
        int columnSize = isSameCategory(notIncludedRows);
        if (columnSize != -1) {
            return new InformationGainNode(null, columnSize);
        }
        int bestAttr = getWinnerAttribute(ignoredCols, notIncludedRows);
        ArrayList<Integer> baseColumn = new ArrayList<Integer>(ignoredCols);
        baseColumn.add(bestAttr);
        InformationGainNode[] subsets = new InformationGainNode[numberOfAttributeTypes[bestAttr]];
        for (int i = 0; i < subsets.length; i++) {

            ArrayList<Integer> baseRow = addnotIncludedRows(bestAttr, i, notIncludedRows);
            subsets[i] = generateDecisionTree(baseColumn, baseRow);
            if (subsets[i] == null) {
                subsets[i] = new InformationGainNode(null, getMostCommonAttribute(notIncludedRows));
            }
        }
        return new InformationGainNode(subsets, bestAttr);
    }

    private ArrayList<Integer> addnotIncludedRows(int attribute, int sId, ArrayList<Integer> notIncludedRows) {
        String string = userInput[attribute][sId];
        if (string == null)
            return notIncludedRows;
        ArrayList<Integer> newIgnored = new ArrayList<Integer>(notIncludedRows);
        for (int userInputScenario = 1; userInputScenario < userTempData.length; userInputScenario++) {
     
            if (!string.equals(userTempData[userInputScenario][attribute]) && !isNotIncluded(userInputScenario, notIncludedRows)) {
                newIgnored.add(userInputScenario);
            }
        }
        return newIgnored;
    }

    private int isSameCategory(ArrayList<Integer> notIncludedRows) {
        int columnSize = -1;
        boolean found = false;
        for (int userInputScenario = 1; userInputScenario < userTempData.length || !found; userInputScenario++) {
            if (isNotIncluded(userInputScenario, notIncludedRows))
                continue;
            String clsString = userTempData[userInputScenario][userTempData[0].length - 1];
            for (int columnsId = 0; columnsId < numberOfAttributeTypes[userTempData[0].length - 1]; columnsId++) {
                if (clsString.equals(userInput[userTempData[0].length - 1][columnsId])) {
                    columnSize = columnsId;
                    found = true;
                    break;
                }
            }
        }
        String clsString = userInput[userTempData[0].length - 1][columnSize];
        for (int userInputScenario = 1; userInputScenario < userTempData.length; userInputScenario++) {
            if (isNotIncluded(userInputScenario, notIncludedRows))
                continue;
            if (!clsString.equals(userTempData[userInputScenario][userTempData[0].length - 1])) {
                return -1;
            }
        }
        return columnSize;
    }

    private int getMostCommonAttribute(ArrayList<Integer> notIncludedRows) {
        int attributesColumnCount = userTempData[0].length - 1;
        int[] columnsCount = new int[numberOfAttributeTypes[attributesColumnCount]];
        for (int userInputScenario = 1; userInputScenario < userTempData.length; userInputScenario++) {
            if (isNotIncluded(userInputScenario, notIncludedRows))
                continue;
            String cellClass = userTempData[userInputScenario][attributesColumnCount];
            for (int columnSize = 0; columnSize < columnsCount.length; columnSize++) {
                if (cellClass.equals(userInput[attributesColumnCount][columnSize])) {
                    columnsCount[columnSize]++;
                }
            }
        }
        int mostCommonId = 0;
        for (int columnSize = 1; columnSize < columnsCount.length; columnSize++) {
            if (columnsCount[columnSize] > columnsCount[mostCommonId]) {
                mostCommonId = columnSize;
            }
        }
        return mostCommonId;
    }

    private int getWinnerAttribute(ArrayList<Integer> notIncludedCloumns, ArrayList<Integer> notIncludedRows) {
        double s = getState(notIncludedRows);
        int[][][] columnCount = getNumberOfClassifiers(notIncludedCloumns, notIncludedRows);
        int totalRows = userTempData.length - 1 - notIncludedRows.size();
        double winnerGain = -1;
        int winnerAttribute = -1;
        
        for (int attribute = 0; attribute < columnCount.length; attribute++) {
            if (isNotIncluded(attribute, notIncludedCloumns))
                continue;
            double gain = s;
            for (int[] string : columnCount[attribute]) {
                int stringTotal = 0; 
                for (int classSum : string) {
                    stringTotal += classSum;
                }
                double entropy = 0.0;
                for (int classSum : string) {
                    double cfos = (double) classSum / (double) stringTotal;
                    entropy -= calculate.logBaseTwo(cfos);
                }
                double ratio = ((double) stringTotal / (double) totalRows);
                gain -= ratio * entropy;
            }
            if (gain > winnerGain) {
    
                winnerGain = gain;
                winnerAttribute = attribute;
            }
        }
        return winnerAttribute;
    }

    private boolean isNotIncluded(int number, ArrayList<Integer> notIncludedList) {
        boolean flag = false;
        for (Integer notIncluded : notIncludedList) {
            if (number == notIncluded) {
                flag = true;
            break;
            }
        }
        return flag;
    }

    private int[][][] getNumberOfClassifiers(ArrayList<Integer> ignoredCols, ArrayList<Integer> notIncludedRows) {
        int attributesColumnCount = userTempData[0].length - 1; 
        int classes = numberOfAttributeTypes[attributesColumnCount];
        int[][][] columnCount = new int[attributesColumnCount][][];
        for (int attribute = 0; attribute < attributesColumnCount; attribute++) {

            if (isNotIncluded(attribute, ignoredCols))
                continue;
            int attrStrings = numberOfAttributeTypes[attribute];
            columnCount[attribute] = new int[attrStrings][classes];
            for (int userInputScenario = 1; userInputScenario < userTempData.length; userInputScenario++) {
                if (isNotIncluded(userInputScenario, notIncludedRows))
                    continue;
                String cell = userTempData[userInputScenario][attribute];
                String rowClass = userTempData[userInputScenario][attributesColumnCount];
                int stringId = 0;
                for (String attributeValue : userInput[attribute]) {
                    if (attributeValue == null)
                        continue;
                    if (cell.equals(attributeValue)) {
                        int classId = 0;
                        for (String columnSize : userInput[attributesColumnCount]) {
                            if (columnSize == null)
                                continue;
                            if (rowClass.equals(columnSize)) {
                                columnCount[attribute][stringId][classId]++;
                            }
                            classId++;
                        }
                    }
                    stringId++;
                }
            }
        }
        return columnCount;
    }

    private double getState(ArrayList<Integer> notIncludedRows) {
        int attributesColumnCount = userTempData[0].length - 1;
        int[] attributesCount = new int[numberOfAttributeTypes[attributesColumnCount]];
        for (int userInputScenario = 1; userInputScenario < userTempData.length; userInputScenario++) {
            if (isNotIncluded(userInputScenario, notIncludedRows))
                continue;
            String rowColumns = userTempData[userInputScenario][attributesColumnCount];
            for (int c = 0; c < attributesCount.length; c++) {
                if (rowColumns.equals(userInput[attributesColumnCount][c])) {
                    attributesCount[c]++;
                }
            }
        }
        double s = 0.0;
        int totalRows = userTempData.length - 1 - notIncludedRows.size();
        for (Integer sum : attributesCount) {
            double fraction = ((double) sum / totalRows);
            s -= calculate.logBaseTwo(fraction);
        }
        return s;
    }

    void convertArray(String[][] inputData) {
        userTempData = inputData;
        numberOfAttributeTypes = new int[userTempData[0].length];
        userInput = new String[userTempData[0].length][userTempData.length];
        int index = 0;
        for (int attribute = 0; attribute < userTempData[0].length; attribute++) {
            numberOfAttributeTypes[attribute] = 0;
            for (int ex = 1; ex < userTempData.length; ex++) {
                for (index = 0; index < numberOfAttributeTypes[attribute]; index++)
                    if (userTempData[ex][attribute].equals(userInput[attribute][index]))
                        break; 
                if (index == numberOfAttributeTypes[attribute]) 
                    userInput[attribute][numberOfAttributeTypes[attribute]++] = userTempData[ex][attribute];
            }
        } 
    
    } 



}