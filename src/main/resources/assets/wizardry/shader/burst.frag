#version 120

uniform int time;
uniform int count;

float millis = float(time);
int randID = 0;

float rand(int n) {
  return fract(sin(float(n)) * 43758.5453123);;
}

vec4 godSparkleSimple( vec2 uv )
{
    vec4 color = gl_Color;
    color.w = 0;
    
    float lenSq = uv.x * uv.x + uv.y * uv.y;
    
    vec2 check = vec2(0, 1);
    
    float dotP = check.x*uv.x + check.y*uv.y;
    float detP = check.x*uv.y - check.y*uv.x;
    
    float angle = degrees(atan(detP, dotP));
    angle += 180.;
    angle += 0.2 * 0.001 * millis;
    
    for(int i = 0; i < count; i++) {
        float startAngle = 360. * rand(randID++);
        float angleWidth = 20. + 40. * rand(randID++);
        float speed = 0.2 * ( rand(randID++) - 0.45 );
        
        startAngle += speed * 0.001 * millis;
        
        float checkAngle = angle - startAngle;
        
        checkAngle = mod(checkAngle, angleWidth*2.);
        float dist = 0.25 + 0.25 * rand(randID++);
        
        if(checkAngle < angleWidth) {
            vec4 ourColor = gl_Color;
            
            float distPercent = sqrt(lenSq)/dist;
            ourColor.w = 1.-distPercent;
            
        	color = mix(color, ourColor, ourColor.w);
    	}
    }
    
    return color;
}

void main()
{
    vec2 uv = vec2(gl_TexCoord[0]);
    if(uv.x > 1) {
        randID = int(uv.x);
        uv.x = fract(uv.x);
    }
    uv -= vec2(0.5);
    gl_FragColor = godSparkleSimple(uv);
}