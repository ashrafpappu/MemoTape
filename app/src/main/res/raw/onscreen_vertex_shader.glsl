attribute vec2 a_vPosition;
attribute vec2 a_texCoord;
varying vec2 v_texCoord;
void main()
{
	gl_Position = vec4(a_vPosition, 0.0, 1.0);
	v_texCoord = a_texCoord;
}