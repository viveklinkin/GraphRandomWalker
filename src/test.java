import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class test {
	static int num = 68;
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		List<String> lines1 = new ArrayList<String>();
		
		for(int i = 0;i <= num; i++) {
			for(int j = 0; j <= num; j++) {
				if(i != j) {
					String s = i + "," + j + "," + Math.random() * 10;
					lines1.add(s);
				}
			}
		}
		
		BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\Users\\vivek\\eclipse-workspace\\GraphRandomWalker\\srcedges.csv"));
		
		for(int i = 0; i < lines1.size(); i++) {
			bw.write(lines1.get(i));
			bw.write("\n");
		}
		bw.flush();
		bw.close();
		System.out.println("done " + lines1.size());
	}

}
