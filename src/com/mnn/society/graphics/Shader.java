package com.mnn.society.graphics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;

import com.mnn.society.server.utils.Logger;

/**
 * Shaders!!
 * When you want to load a shader program into the graphics card, this is the class to do it!
 * @author mickeman
 *
 */
public class Shader {
	public int programId = 0;
	public boolean valid = false;
	public int vertShaderId, fragShaderId;
	
	public Shader(String vertShader, String fragShader) throws Exception {
		this.programId = ARBShaderObjects.glCreateProgramObjectARB();
		this.valid = true;
		
		if (this.programId != 0) {
            if (!vertShader.equalsIgnoreCase(""))
            	this.vertShaderId = createVertShader("shaders\\"+vertShader+".vert");
            
            if (!fragShader.equalsIgnoreCase(""))
            	this.fragShaderId = createFragShader("shaders\\"+fragShader+".frag");
        }
        else valid = false;
		
		/*
        * if the vertex and fragment shaders setup sucessfully,
        * attach them to the shader program, link the sahder program
        * (into the GL context I suppose), and validate
        */
        if (this.vertShaderId !=0) {
        	ARBShaderObjects.glAttachObjectARB(this.programId, this.vertShaderId);
        }
        if (this.fragShaderId !=0) {
            ARBShaderObjects.glAttachObjectARB(this.programId, this.fragShaderId);
        }
        
        if (this.vertShaderId != 0 || this.fragShaderId != 0) {
            ARBShaderObjects.glLinkProgramARB(this.programId);
            if (ARBShaderObjects.glGetObjectParameteriARB(this.programId, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL_FALSE) {
                printLogInfo(this.programId);
                valid = false;
            }
            ARBShaderObjects.glValidateProgramARB(this.programId);
            if (ARBShaderObjects.glGetObjectParameteriARB(this.programId, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL_FALSE) {
                printLogInfo(this.programId);
                valid = false;
            }
        } else valid = false;
		
        if (!valid) throw new Exception("unable to load shader " + vertShader + " " + fragShader);
	}
	
	/*
    * With the exception of syntax, setting up vertex and fragment shaders
    * is the same.
    * @param the name and path to the vertex shader
    */
    private int createVertShader(String filename){
        //vertShader will be non zero if succefully created
    	this.vertShaderId = ARBShaderObjects.glCreateShaderObjectARB(ARBVertexShader.GL_VERTEX_SHADER_ARB);
        //if created, convert the vertex shader code to a String
        if(this.vertShaderId == 0) { return 0; }
        String vertexCode="";
        String line;
        try {
            BufferedReader reader=new BufferedReader(new FileReader(filename));
            while( (line=reader.readLine())!=null ){
                vertexCode+=line + "\n";
            }
        } catch(Exception e) {
        	Logger.log(Logger.LOG_CLIENT, "CLIENT : Shader : Fail reading vertex shading code");
            return 0;
        }
        
        /*
        * associate the vertex code String with the created vertex shader
        * and compile
        */
        ARBShaderObjects.glShaderSourceARB(this.vertShaderId, vertexCode);
        ARBShaderObjects.glCompileShaderARB(this.vertShaderId);
        //if there was a problem compiling, reset vertShader to zero
        if (ARBShaderObjects.glGetObjectParameteriARB(this.vertShaderId, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL_FALSE) {
            printLogInfo(this.vertShaderId);
            this.vertShaderId = 0;
        }
        //if zero we won't be using the shader
        return this.vertShaderId;
    }

    //same as per the vertex shader except for method syntax
    private int createFragShader(String filename){
        this.fragShaderId = ARBShaderObjects.glCreateShaderObjectARB(ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
        if (this.fragShaderId == 0) { return 0; }
            String fragCode="";
            String line;
        try {
            BufferedReader reader=new BufferedReader(new FileReader(filename));
            while((line=reader.readLine())!=null) {
                fragCode+=line + "\n";
            }
        } catch(Exception e){
        	Logger.log(Logger.LOG_CLIENT, "CLIENT : Shader : Fail reading fragment shading code");
            return 0;
        }
        ARBShaderObjects.glShaderSourceARB(this.fragShaderId, fragCode);
        ARBShaderObjects.glCompileShaderARB(this.fragShaderId);
        if (ARBShaderObjects.glGetObjectParameteriARB(this.fragShaderId, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL_FALSE) {
            printLogInfo(this.fragShaderId);
            this.fragShaderId = 0;
        }
        return this.fragShaderId;
    }
	
	private static boolean printLogInfo(int obj){
        IntBuffer iVal = BufferUtils.createIntBuffer(1);
        ARBShaderObjects.glGetObjectParameterARB(obj,ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB, iVal);

        int length = iVal.get();
        if (length > 1) {
            // We have some info we need to output.
            ByteBuffer infoLog = BufferUtils.createByteBuffer(length);
            iVal.flip();
            ARBShaderObjects.glGetInfoLogARB(obj, iVal, infoLog);
            byte[] infoBytes = new byte[length];
            infoLog.get(infoBytes);
            String out = new String(infoBytes);
            Logger.log(Logger.LOG_CLIENT, "CLIENT : Shader : Info log:\n"+out);
        }
        else return true;
        return false;
    }
	
	public void preRender() {
		if(this.valid)
			ARBShaderObjects.glUseProgramObjectARB(this.programId);
	}
	
	public static void clearAllShaders() {
		ARBShaderObjects.glUseProgramObjectARB(0);
	}
}
