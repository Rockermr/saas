package com.altimetrik.logiccontroller;

import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.concurrent.TimeUnit;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripperByArea;

public class ExtractText {

	public List<String> extractedData = new ArrayList<String>();

	public List<String> pdfExtractor(List<InputStream> allPdfStream) throws IOException, InterruptedException {
		ExtractText pdfObject = new ExtractText();

		Iterator<InputStream> iterator = allPdfStream.iterator();
		ExecutorService executor = Executors.newFixedThreadPool(10);
		PDDocument document = null;
		InputStream pdfStream = null;
		while (iterator.hasNext()) {
			pdfStream = iterator.next();
			document = PDDocument.load(pdfStream);

			int count = document.getNumberOfPages();

			for (int j = 0; j < count; j = j + 2) {

				Runnable worker = new MyRunnable(document, j, pdfObject);

				executor.execute(worker);

			}

		}
		try {

			executor.awaitTermination(3, TimeUnit.SECONDS);
			executor.shutdownNow();

		}

		finally {
			if (pdfStream != null) {
				pdfStream.close();
			}
			if (document != null) {
				document.close();
			}

		}

		return pdfObject.extractedData;

	}
}

class MyRunnable implements Runnable {

	int[][] pts = { { 0, 130, 150, 10 }, { 130, 130, 160, 8 }, { 260, 148, 140, 8 }, { 285, 172, 200, 60 } };

	private PDDocument document;
	private int j;
	private ExtractText pdfObject;

	MyRunnable(PDDocument document, int j, ExtractText pdfObject) {
		this.document = document;
		this.j = j;
		this.pdfObject = pdfObject;

	}

	@Override
	public void run() {

		try {
			List<String> list = new ArrayList<String>();

			PDFTextStripperByArea stripper = new PDFTextStripperByArea();
			stripper.setSortByPosition(true);
			for (int i = 0; i < 4; i++) {
				Rectangle rect = new Rectangle(pts[i][0], pts[i][1], pts[i][2], pts[i][3]);
				stripper.addRegion("class1", rect);
				stripper.extractRegions(document.getPage(j));

				list.add(stripper.getTextForRegion("class1"));
			}

			for (int i = 0, y = 382; i < 20; i++, y = y + 10) {

				Rectangle rect = new Rectangle(370, y, 550, 8);
				stripper.addRegion("class1", rect);
				stripper.extractRegions(document.getPage(j));

				if (stripper.getTextForRegion("class1").contains("Total Invoice")) {
					list.add(stripper.getTextForRegion("class1").replace("Total Invoice $", ""));

					break;
				}
			}

			pdfObject.extractedData.addAll(list);

		} catch (IOException e) {
			System.out.print("Error in Thread!");
		}

	}
}
