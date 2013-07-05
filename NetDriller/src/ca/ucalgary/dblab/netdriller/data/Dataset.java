package ca.ucalgary.dblab.netdriller.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import cern.colt.list.DoubleArrayList;

public class Dataset {
	double[][] matrix;
	int numOfObjects;
	int featureNum = 0;
	String[][] headers;
	
	public boolean openFile(String path, String delimiter, boolean hasNames)
	{
		Vector<String> labels = new Vector<String>();
		BufferedReader reader;
		String currentLine;
		List<DoubleArrayList> rows = new ArrayList<DoubleArrayList>();
		
		try {
			reader = new BufferedReader(new FileReader(new File(path)));
			if(hasNames)
			{
				headers = new String[2][];
				currentLine = reader.readLine();
				headers[0] = currentLine.split(delimiter);
				featureNum = headers[0].length;
			}
			while ((currentLine = reader.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(currentLine,delimiter);
				if (tokenizer.countTokens() == 0) {
					break;
				}
				DoubleArrayList currentRow = new DoubleArrayList();
				if(hasNames)
					labels.add(tokenizer.nextToken());
				while (tokenizer.hasMoreTokens()) {
					String token = tokenizer.nextToken();
					if (token.equals("") || token.equals(" "))
						continue;
					try{
					currentRow.add(Double.parseDouble(token));
					} catch (NumberFormatException nfe){
						return false;
					}
				}
				if(featureNum == 0)
					featureNum = currentRow.size();
				if (featureNum != currentRow.size())
					return false;
				rows.add(currentRow);
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
		numOfObjects = rows.size();
		if(hasNames)
		{
			headers[1] = new String[labels.size()];
			for(int i = 0; i < labels.size(); i++)
				headers[1][i] = labels.get(i);
		}
		createMatrix(rows);
		if(matrix == null)
			return false;
		return true;
	}
	
	private void createMatrix(List<DoubleArrayList> rows)
	{
		matrix = new double[numOfObjects][featureNum];
		for(int i = 0; i < numOfObjects; i++)
		{
			for(int j = 0; j < featureNum; j++)
			{
				try{
				matrix[i][j] = rows.get(i).get(j);
				} catch(ArrayIndexOutOfBoundsException e){
					matrix = null;
				}
			}
		}
	}

	public double[] getRow(int rowInd)
	{
		return matrix[rowInd];
	}
	public double[][] getMatrix() {
		return matrix;
	}

	public int getNumOfObjects() {
		return numOfObjects;
	}

	public int getFeatureNum() {
		return featureNum;
	}

	public String[][] getHeaders() {
		return headers;
	}
	
	
}
