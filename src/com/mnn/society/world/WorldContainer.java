package com.mnn.society.world;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.mnn.society.server.utils.Logger;

public class WorldContainer {
	public short [][][] chunksInGraphics 	= new short[3][3][6400];
	public short [][] chunksInMemory 		= new short[16][6400];
	
	public int maxXChunk = 50;
	public int maxYChunk = 25;
	
	public WorldContainer (int originx, int originy) {
		loadChunksIntoMemory(originx, originy);
	}
	
	/**
	 * Based on the origin, will load all surrounding chunks and the origin into the graphics array and memory array
	 * @param ox
	 * @param oy
	 */
	public void loadChunksIntoMemory (int ox, int oy) {
		int tmp [] = null;
		int memindex = 0;
		//set chunks in memory
		//set chunks in graphics
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				int currentx = (ox-2 < 0) ? (50-ox-2) : ox-2 + j;
				int currenty = (oy-2 < 0) ? (50-oy-2) : oy-2 + i;
				
				if (currentx + j >= 50) currentx = (currentx + j) - 50;
				if (currenty + i >= 25) currenty = (currenty + i) - 25;
				
				//into memory : j = 0, i = 0, i = 4, j = 4
				if (j == 0 || j == 4 || i == 0 || i == 4) {
					try {
						BufferedImage img = readImage("res\\worldchunks\\"+currentx+"_"+currenty+".png");
						tmp = new int[6400];
						img.getRGB(0, 0, 80, 80, tmp, 0, 80); //Get all pixels
						for (int k = 0; k < tmp.length; k++) {
							int col = tmp[k];
							int r = 10 * (int)(((float)((col >> 16) & 0xFF)) / 255f); 
							this.chunksInMemory[memindex][k] = (short)r;
						}
					}
					catch (Exception e) {
						Logger.log(Logger.LOG_CLIENT, "CLIENT : WorldContainer : loadChunksIntoMemory : memoryArray : exception : " + e);
					}
				}
				else {
					try {
						BufferedImage img = readImage("res\\worldchunks\\"+currentx+"_"+currenty+".png");
						tmp = new int[6400];
						img.getRGB(0, 0, 80, 80, tmp, 0, 80); //Get all pixels
						for (int k = 0; k < tmp.length; k++) {
							int argb = tmp[k];
							
							int r = (argb >> 16) & 0xff;
			                int g = (argb >>  8) & 0xff;
			                int b = (argb      ) & 0xff;

			                int l = (int)(((float) (.299 * r + .587 * g + .114 * b)) / 8f);
							
							//int r = 10 * (int)(((float)((col >> 16) & 0xFF)) / 255f); 
							//int r = 10 - (int)(((float)((col >> 24) & 0xFF))/25.5f);
							//int r = 10 * (int)(((float)col / (float)(255*255*255*255)));
							this.chunksInGraphics[j-1][i-1][k] = (short)l;
							
							//System.out.println("r: " + l);
							
							//int g = 10 * (int)(((float)((col >> 8) & 0xFF)) / 255f);
							//int b = 10 * (int)(((float)((col) & 0xFF)) / 255f);
						}
					}
					catch (Exception e) {
						Logger.log(Logger.LOG_CLIENT, "CLIENT : WorldContainer : loadChunksIntoMemory : graphicsArray : exception : " + e);
					}
				}
			}
		}
		
		Logger.log(Logger.LOG_CLIENT, "CLIENT : WorldContainer : loaded chunks : ");
		
		for (int i = 0; i < this.chunksInGraphics.length; i++) {
			for (int j = 0; j < this.chunksInGraphics[i].length; j++) {
				Logger.log(Logger.LOG_CLIENT, "CLIENT : WorldContainer : chunksInGraphics ["+i+"]["+j+"]: " + this.chunksInGraphics[i][j].length);
			}
		}
		
		for (int i = 0; i < this.chunksInMemory.length; i++) {
			Logger.log(Logger.LOG_CLIENT, "CLIENT : WorldContainer : chunksInMemory ["+i+"]: " + this.chunksInMemory[i].length);
		}
	}
	
	
	public BufferedImage readImage (String filename) throws IOException {
		Logger.log(Logger.LOG_CLIENT, "CLIENT : WorldContainer : loading image : " + filename);
	    
		try {
			File file = new File(filename);
			InputStream is = new FileInputStream(file);
			return ImageIO.read(new BufferedInputStream(is));
		}
		catch (IOException e) {
			throw new IOException("io: unable to load : "+filename);
		}
		catch (Exception e) {
			throw new IOException("unable to load : "+filename);
		}
	}
}
