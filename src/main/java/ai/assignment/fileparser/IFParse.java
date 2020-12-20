package ai.assignment.fileparser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

public final class IFParse {
    
    private static final String INPUT_TEXT_FILE_NAME = "/input.txt";
    
    
    public String[][] getUserInput() throws FileNotFoundException, IOException {
        InputStream in = this.getClass().getResourceAsStream(INPUT_TEXT_FILE_NAME);

        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String s = br.readLine();
        StringTokenizer st = new StringTokenizer(s, ",");
        int numberOfExamples = 1;
        while (br.readLine() != null) {
            numberOfExamples++;
        }
        String[][] data = new String[numberOfExamples][st.countTokens()];
        int numberOfAttributes = st.countTokens();
        in = this.getClass().getResourceAsStream(INPUT_TEXT_FILE_NAME);
        br = new BufferedReader(new InputStreamReader(in));
        String s1 = "";
        int i=0;
        while((s1=br.readLine())!=null) {
            st = new StringTokenizer(s1, ",");
            for (int j = 0; j < numberOfAttributes; j++) {
                data[i][j] = st.nextToken();
                

            }
            i++;
        }
        
            System.out.println(Arrays.deepToString(data));
        return data;

    }

}
