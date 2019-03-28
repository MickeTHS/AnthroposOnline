uniform float time;
//uniform float resolution_x;
//uniform float resolution_y;
uniform vec2 resolution;


float makePoint(float x,float y,float fx,float fy,float sx,float sy,float t){
   float xx=x+sin(t*fx*0.1)*sx;
   float yy=y+cos(t*fy*0.1)*sy;
   return 1.0/sqrt(xx*xx+yy*yy);
}

void main( void ) {
	//vec2 p=(gl_FragCoord.xy/resolution_x)*2.0-vec2(1.0,resolution_y/resolution_x);
	vec2 p=(gl_FragCoord.xy/resolution.x)*2.0-vec2(1.0,resolution.y/resolution.x);
	//p=p*1.0;

	float x=p.x;
	float y=p.y;

	float a = makePoint(x, y, 0.5, 0.5, 0.5, 0.5, 1);
	float b = makePoint(x, y, 1.2, 1.9, 0.1, 0.2, 1);
	float c = makePoint(x, y, 3.7, 0.3, 0.3, 0.3, 1);
	
	vec3 d = vec3(a, b, c) / 100.0;
	
	gl_FragColor = vec4(d.x,d.y,d.z,1.0);
}