package ai.assignment.decisiontree.entity;
public class InformationGainNode {
    

        InformationGainNode[] informationGainNodes;
        int treeNodeValue;

        public InformationGainNode(InformationGainNode[] informationGainNodes, int treeNodeValue) {
            this.treeNodeValue = treeNodeValue;
            this.informationGainNodes = informationGainNodes;
        }

        public String convertToString(String spacing, String[][] userTempData, String[][] userInput) {
            if (informationGainNodes != null) {
                String s = "";
                for (int i = 0; i < informationGainNodes.length; i++)
                    s += spacing + userTempData[0][treeNodeValue] + "=" + userInput[treeNodeValue][i] + "\n" + informationGainNodes[i].convertToString(spacing + "  ", userTempData, userInput);
                return s;
            } else
                return spacing + "Decision: " + userInput[userTempData[0].length - 1][treeNodeValue] + "\n\n";
        } 

    } 

