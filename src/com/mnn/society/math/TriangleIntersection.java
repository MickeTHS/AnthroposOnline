package com.mnn.society.math;

public class TriangleIntersection {
	public static float FABS(float x) {
		return (x>=0?x:-x);
	}
	
	public static float [] cross(float [] v0, float [] v1) {
		return new float [] {	v0[1] * v1[2] - v0[2] * v1[1], 
				  				v0[2] * v1[0] - v0[0] * v1[2],
				  				v0[0] * v1[1] - v0[1] * v1[0] };
	}

	public static float [] sVpsV_2(float s1, float [] V1, float s2, float [] V2) {
		return new float [] { s1*V1[0] + s2*V2[0], s1*V1[1] + s2*V2[1] };
	}
	
	public static float [] myVpV(float [] v2, float [] v1) {
		return new float [] { v2[0]+v1[0], v2[1]+v1[1], v2[2]+v1[2] };
	}
	
	public static float [] myVmV(float [] v2, float [] v1) {
		return new float [] { v2[0]-v1[0], v2[1]-v1[1], v2[2]-v1[2] };
	}
	
	public static int tr_tri_intersect3D (float [] C1, float [] P1, float [] P2, float [] D1, float [] Q1, float [] Q2) {
		float [] t	 	= new float[3];
		float [] p1 	= new float[3];
		float [] p2 	= new float[3];
		float [] r 	= new float[3];
		float [] r4 	= new float[3];
		
		float beta1, beta2, beta3;
		float gama1, gama2, gama3;
		float det1, det2, det3;
		float dp0, dp1, dp2;
		float dq1,dq2,dq3,dr, dr3;
		float alpha1, alpha2;
		boolean alpha1_legal, alpha2_legal;
		float  SF = 0;
		boolean beta1_legal, beta2_legal;
		
		r = myVmV(D1,C1);
		// determinant computation	
		dp0 = P1[1]*P2[2]-P2[1]*P1[2];
		dp1 = P1[0]*P2[2]-P2[0]*P1[2];
		dp2 = P1[0]*P2[1]-P2[0]*P1[1];
		dq1 = Q1[0]*dp0 - Q1[1]*dp1 + Q1[2]*dp2;
		dq2 = Q2[0]*dp0 - Q2[1]*dp1 + Q2[2]*dp2;
		dr  = -r[0]*dp0  + r[1]*dp1  - r[2]*dp2;
		
		
		beta1 = dr*dq2;  // beta1, beta2 are scaled so that beta_i=beta_i*dq1*dq2
		beta2 = dr*dq1;
		beta1_legal = (beta2>=0) && (beta2 <=dq1*dq1) && (dq1 != 0);
		beta2_legal = (beta1>=0) && (beta1 <=dq2*dq2) && (dq2 != 0);
			
		dq3=dq2-dq1;
		dr3=+dr-dq1;   // actually this is -dr3
		
		if ((dq1 == 0) && (dq2 == 0))
		{
			if (dr!=0) return 0;  // triangles are on parallel planes
			else
			{						// triangles are on the same plane
				float [] C2 = new float[3];
				float [] C3 = new float[3];
				float [] D2 = new float[3];
				float [] D3 = new float[3];
				float [] N1 = new float[3];
				
				// We use the coplanar test of Moller which takes the 6 vertices and 2 normals  
				//as input.
				C2 = myVpV(C1,P1);
				C3 = myVpV(C1,P2);
				D2 = myVpV(D1,Q1);
				D3 = myVpV(D1,Q2);
				N1 = cross(P1,P2);
				return coplanar_tri_tri(N1,C1, C2,C3,D1,D2,D3);
			}
		}

		else if (!beta2_legal && !beta1_legal) return 0;// fast reject-all vertices are on
														// the same side of the triangle plane

		else if (beta2_legal && beta1_legal)    //beta1, beta2
		{
			SF = dq1*dq2;
			t = sVpsV_2(beta2,Q2, (-beta1),Q1);
		}
		
		else if (beta1_legal && !beta2_legal)   //beta1, beta3
		{
			SF = dq1*dq3;
			beta1 =beta1-beta2;   // all betas are multiplied by a positive SF
			beta3 =dr3*dq1;
			t = sVpsV_2((SF-beta3-beta1),Q1,beta3,Q2);
		}
		
		else if (beta2_legal && !beta1_legal) //beta2, beta3
		{
			SF = dq2*dq3;
			beta2 =beta1-beta2;   // all betas are multiplied by a positive SF
			beta3 =dr3*dq2;
			t = sVpsV_2((SF-beta3),Q1,(beta3-beta2),Q2);
			Q1=Q2;
			beta1=beta2;
		}
		r4 = sVpsV_2(SF,r,beta1,Q1);
		//seg_collide3(t,r4);  // calculates the 2D intersection
		
		float [] q = t;
		float [] _r = r4;
		
		p1[0]=SF*P1[0];
		p1[1]=SF*P1[1];
		p2[0]=SF*P2[0];
		p2[1]=SF*P2[1];
		det1 = p1[0]*q[1]-q[0]*p1[1];
		gama1 = (p1[0]*_r[1]-_r[0]*p1[1])*det1;
		alpha1 = (_r[0]*q[1] - q[0]*_r[1])*det1;
		alpha1_legal = (alpha1>=0) && (alpha1<=(det1*det1)  && (det1!=0));
		det2 = p2[0]*q[1] - q[0]*p2[1];
		alpha2 = (_r[0]*q[1] - q[0]*_r[1]) *det2;
		gama2 = (p2[0]*_r[1] - _r[0]*p2[1]) * det2;
		alpha2_legal = (alpha2>=0) && (alpha2<=(det2*det2) && (det2 !=0));
		det3=det2-det1;
		gama3=((p2[0]-p1[0])*(_r[1]-p1[1]) - (_r[0]-p1[0])*(p2[1]-p1[1]))*det3;
		
		if (alpha1_legal) {
			if (alpha2_legal) {
				if ( ((gama1<=0) && (gama1>=-(det1*det1))) || ((gama2<=0) && (gama2>=-(det2*det2))) || (gama1*gama2<0)) return 12;
			}
			else {
				if ( ((gama1<=0) && (gama1>=-(det1*det1))) || ((gama3<=0) && (gama3>=-(det3*det3))) || (gama1*gama3<0)) return 13;
			}
		}
		else if (alpha2_legal) {
			if ( ((gama2<=0) && (gama2>=-(det2*det2))) || ((gama3<=0) && (gama3>=-(det3*det3))) || (gama2*gama3<0)) return 23;
		}
		
		return 0;
	}
	
	public static int EDGE_AGAINST_TRI_EDGES(float [] V0, float [] V1, float [] U0, float [] U1, float [] U2, short i0, short i1) {
		float Ax,Ay,Bx=0,By=0,Cx=0,Cy=0,e=0,d=0,f=0;
		Ax=V1[i0]-V0[i0];
		Ay=V1[i1]-V0[i1];
		
		int ret = -1;
		
		/* test edge U0,U1 against V0,V1 */
		if( (ret = EDGE_EDGE_TEST(V0,U0,U1,Ax,Ay,Bx,By,Cx,Cy,e,d,f,i0,i1)) != -1) return ret;
		/* test edge U1,U2 against V0,V1 */
		if( (ret = EDGE_EDGE_TEST(V0,U1,U2,Ax,Ay,Bx,By,Cx,Cy,e,d,f,i0,i1)) != -1) return ret;
		/* test edge U2,U1 against V0,V1 */
		if( (ret = EDGE_EDGE_TEST(V0,U2,U0,Ax,Ay,Bx,By,Cx,Cy,e,d,f,i0,i1)) != -1) return ret;
		
		return -1;
	}
	
	public static int EDGE_EDGE_TEST(	float [] V0, float [] U0, float [] U1,
										float Ax, float Ay, float Bx, float By, 
										float Cx, float Cy, float e, float d, float f,
										short i0, short i1) {
		Bx=U0[i0]-U1[i0];
		By=U0[i1]-U1[i1];
		Cx=V0[i0]-U0[i0];
		Cy=V0[i1]-U0[i1];
		f=Ay*Bx-Ax*By;
		d=By*Cx-Bx*Cy;
		
		if((f>0 && d>=0 && d<=f) || (f<0 && d<=0 && d>=f)) {
			e=Ax*Cy-Ay*Cx;
		}
	    if(f>0) {
	    	if(e>=0 && e<=f) return 1;
	    }
	    else {
	    	if(e<=0 && e>=f) return 1;
	    }
	    
	    return -1;
	}
	
	
	public static int POINT_IN_TRI(float [] V0, float [] U0, float [] U1, float [] U2, short i0, short i1) {
		float a,b,c,d0,d1,d2;
		/* is T1 completly inside T2? */
		/* check if V0 is inside tri(U0,U1,U2) */
		a=U1[i1]-U0[i1];
		b=-(U1[i0]-U0[i0]);
		c=-a*U0[i0]-b*U0[i1];
		d0=a*V0[i0]+b*V0[i1]+c;
		
		a=U2[i1]-U1[i1];
		b=-(U2[i0]-U1[i0]);
		c=-a*U1[i0]-b*U1[i1];
		d1=a*V0[i0]+b*V0[i1]+c;
		
		a=U0[i1]-U2[i1];
		b=-(U0[i0]-U2[i0]);
		c=-a*U2[i0]-b*U2[i1];
		d2=a*V0[i0]+b*V0[i1]+c;
		if(d0*d1>0.0) {
			if(d0*d2>0.0) return 0;
		}
		
		return -1;
	}

	//This procedure testing for intersection between coplanar triangles is taken 
	// from Tomas Moller's
	//"A Fast Triangle-Triangle Intersection Test",Journal of Graphics Tools, 2(2), 1997
	public static int coplanar_tri_tri(	float [] N, 	float [] V0, 	float [] V1, float [] V2,
										float [] U0,	float [] U1,	float [] U2) {
		float [] A = new float[3];
		short i0,i1;
		
		/* first project onto an axis-aligned plane, that maximizes the area */
		/* of the triangles, compute indices: i0,i1. */
		A[0]=FABS(N[0]);
		A[1]=FABS(N[1]);
		A[2]=FABS(N[2]);
		
		if(A[0]>A[2]) {
			if(A[0]>A[2]) {
				i0=1;      /* A[0] is greatest */
				i1=2;
			}
			else {
				i0=0;      /* A[2] is greatest */
				i1=1;
			}
		}
		else {
			if(A[2]>A[1]) {
				i0=0;      /* A[2] is greatest */
				i1=1;                                           
			}
			else {
				i0=0;      /* A[1] is greatest */
				i1=2;
			}
		}
	    
		int ret = -1;
		
	    /* test all edges of triangle 1 against the edges of triangle 2 */
	    if( (ret = EDGE_AGAINST_TRI_EDGES(V0,V1,U0,U1,U2,i0,i1)) != -1) return ret;
	    if( (ret = EDGE_AGAINST_TRI_EDGES(V1,V2,U0,U1,U2,i0,i1)) != -1) return ret;
	    if( (ret = EDGE_AGAINST_TRI_EDGES(V2,V0,U0,U1,U2,i0,i1)) != -1) return ret;
	                
	    /* finally, test if tri1 is totally contained in tri2 or vice versa */
	    if( (ret = POINT_IN_TRI(V0,U0,U1,U2,i0,i1)) != -1) return ret;
	    if( (ret = POINT_IN_TRI(U0,V0,V1,V2,i0,i1)) != -1) return ret;

	    return 0;
	}
}