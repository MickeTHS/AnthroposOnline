vec3 getcolor(float height, vec3 start, vec3 end) {
	float u = 1.0 - height;
	return vec3(start.x * u + end.x * height, start.y * u + end.y * height, start.z * u + end.z * height);
}

void main() {
	
	vec3 firegradient[8];
	int index = 0;
	
  	firegradient[0] = vec3( 0.97, 0.96, 0.77 );
  	firegradient[1] = vec3( 0.96, 0.89, 0.07 );
  	firegradient[2] = vec3( 0.94, 0.41, 0.15 );
  	firegradient[3] = vec3( 0.75, 0.03, 0.01 );
  	firegradient[4] = vec3( 0.44, 0.0, 0.0 );
  	firegradient[5] = vec3( 0.29, 0.0, 0.0 );
  	firegradient[6] = vec3( 0.05, 0.0, 0.0 );
  	firegradient[7] = vec3( 0.0, 0.0, 0.0 );
	
	float i = (1.0-gl_TexCoord[0].x) * 7.0;
	float rest = i - float(int(i));
	
	vec4 res;
	
	if (rest == 0.0 || rest == 1.0) {
		res = vec4(firegradient[int(i)], 1.0);
	}
	else {
		res = vec4(getcolor(rest, firegradient[int(i)], firegradient[int(i)+1]), 1.0);
	}
	
	gl_FragColor = res;
}