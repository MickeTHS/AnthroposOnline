vec3 getcolor(float height, vec3 start, vec3 end) {
	float u = 1.0 - height;
	return vec3(start.x * u + end.x * height, start.y * u + end.y * height, start.z * u + end.z * height);
}

void main() {
	
	vec3 watergradient[8];
	
  	watergradient[0] = vec3( 1.00, 1.00, 1.00 );
  	watergradient[1] = vec3( 1.00, 1.00, 1.00 );
  	watergradient[2] = vec3( 1.00, 1.00, 1.00 );
  	watergradient[3] = vec3( 1.00, 1.00, 1.00 );
  	watergradient[4] = vec3( 0.00, 0.00, 0.20 ); //darkest
  	watergradient[5] = vec3( 0.00, 0.00, 0.99 ); //lighter
  	watergradient[6] = vec3( 0.70, 0.70, 1.00 ); //beach
  	watergradient[7] = vec3( 1.00, 1.00, 1.00 );
	
	float i = gl_TexCoord[0].x;
	int index = int((i)*10.0);
	float rest = (float(i * 10.0)) - float(index);
	
	vec4 res;
	
	if (rest == 0.0 || rest == 1.0) {
		res = vec4(watergradient[int(i)], 1.0);
	}
	else {
		res = vec4(getcolor(rest, watergradient[index], watergradient[index+1]), 1.0);
	}
	
	gl_FragColor = res;
}