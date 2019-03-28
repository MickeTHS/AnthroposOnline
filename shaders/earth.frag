vec3 getcolor(float height, vec3 start, vec3 end) {
	float u = 1.0 - height;
	return vec3(start.x * u + end.x * height, start.y * u + end.y * height, start.z * u + end.z * height);
}

void main() {
	vec3 earthgradient[10];
	
	earthgradient[0] = vec3( 0.00, 0.00, 0.00 );
	earthgradient[1] = vec3( 0.00, 0.00, 0.00 );
	earthgradient[2] = vec3( 0.00, 0.00, 0.00 );
	earthgradient[3] = vec3( 0.00, 0.80, 0.00 );
	earthgradient[4] = vec3( 0.00, 0.00, 0.00 );
	earthgradient[5] = vec3( 1.00, 0.86, 0.58 );
	earthgradient[6] = vec3( 0.00, 0.50, 0.00 );
	earthgradient[7] = vec3( 0.00, 0.20, 0.00 );
	earthgradient[8] = vec3( 0.50, 0.40, 0.10 );
	earthgradient[9] = vec3( 1.00, 1.00, 1.00 );
	
	float i = gl_TexCoord[0].x;
	int index = int((i)*10.0);
	float rest = (float(i * 10.0)) - float(index);
	
	vec4 res;
	
	if (rest == 0.0) {
		res = vec4(earthgradient[0], 1.0);
	}
	else if (rest == 1.0) {
		res = vec4(earthgradient[9], 1.0);
	}
	else {
		res = vec4(getcolor(rest, earthgradient[index], earthgradient[index+1]), 1.0);
	}
	
	gl_FragColor = res;
}