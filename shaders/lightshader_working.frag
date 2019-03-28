varying vec3 normal;
 
vec4 light0 () {
  vec4 color;
  vec3 lightDir = vec3(gl_LightSource[0].position); 
  vec4 ambient = gl_LightSource[0].ambient * gl_FrontMaterial.ambient;
  vec4 diffuse = gl_LightSource[0].diffuse * max(dot(normal,lightDir),0.0) * gl_FrontMaterial.diffuse;
  color = ambient + diffuse;

  return color;
}

void main() {
  vec4 light;

  if(vec3(gl_LightSource[0].position) != vec3(0.0, 0.0, 0.01))
    light += light0();
  gl_FragColor = light;
}