import java.net.URL;
import java.net.URLEncoder;
import java.io.*;
import java.lang.String;
import com.google.gson.Gson;

class WordResult {
    String word;
    int score;
}

class ex10_rest {
    static private String call_url(String base_url, String function, String[] args) throws Exception {
        String argslist = String.join("&", args);
        URL url = new URL(base_url + "/" + function + "?" + argslist);
        System.out.println("Calling: " + url);
        InputStreamReader is = new InputStreamReader(url.openStream());
        int c;
        String r = "";
        while ((c = is.read()) != -1)
            r += (char) c;
        return r;
    }

    static public void main(String args[]) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter a word pattern: ");
        String input = reader.readLine();

        String encodedInput = URLEncoder.encode(input, "UTF-8");

        String jsonResponse = call_url("https://api.datamuse.com", "words", new String[]{"sp=" + encodedInput});

        Gson gson = new Gson();
        WordResult[] results = gson.fromJson(jsonResponse, WordResult[].class);

        System.out.println("Output:");
        for (WordResult res : results) {
            System.out.println(res.word);
        }
    }
}