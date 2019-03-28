uniform sampler2D sampler01;
uniform sampler2D sampler02;
uniform sampler2D sampler03;
uniform sampler2D sampler04;
uniform float my_value;
//uniform float mousex;
//uniform float mousey;
uniform vec2 mouse;

void main(){
	vec3 tex1=vec3(texture2D(sampler01, (gl_TexCoord[0].xy)));
	vec3 tex2=vec3(texture2D(sampler02, (gl_TexCoord[0].xy)));
	vec3 tex3=vec3(texture2D(sampler03, (gl_TexCoord[0].xy)));
	vec3 tex4=vec3(texture2D(sampler04, (gl_TexCoord[0].xy)));
	
	vec3 color;
		
	if (gl_TexCoord[0].z < 0.4) {
		color = mix(tex3, tex1, gl_TexCoord[0].z*2.5);
	}
	else if (gl_TexCoord[0].z > 0.8) {
		color = mix(tex2, tex4, (gl_TexCoord[0].z-0.8)*5.0);
	}
	else {
		color = mix(tex1, tex2, (gl_TexCoord[0].z-0.4)*2.5);
	}
	
	vec3 mouse_again = vec3(mouse.x, mouse.y, 100.0);
	vec3 position = ( gl_FragCoord.xyz );
	float proximity = distance(position.xyz, mouse_again.xyz);
	
	vec3 shadedColor = vec3(1.0 -(proximity * 0.01));
	
	vec3 blended = vec3(shadedColor + color);
	
	if(blended.x < -0.2 && blended.y < -0.2 && blended.z < -0.2) {
		gl_FragColor = vec4(color/3, 1.0);
	}
	else {
		gl_FragColor = vec4(blended/5 + vec3(0.0, 0.0, my_value), 1.0);
	}
}