#version 120

uniform int time;
uniform int count;
uniform float[16] rands; 

float millis = float(time);

float rand(int n) {
  return rands[int(mod(n, 16.))];
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
    angle += 0.2 * millis;
    
    int randID = 0;
    for(int i = 0; i < count; i++) {
        float startAngle = 360. * rand(randID++);
        float angleWidth = 20. + 40. * rand(randID++);
        float speed = 0.2 * ( rand(randID++) - 0.45 );
        
        startAngle += speed * millis;
        
        float checkAngle = angle - startAngle;
        
        checkAngle = mod(checkAngle, 360.);
        float dist = 0.25 + 0.25 * rand(randID++);
    
        if(lenSq > dist * dist)
            continue;
        
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
    vec2 uv = vec2(gl_TexCoord[0]) - vec2(0.5, 0.5);
    gl_FragColor = godSparkleSimple(uv);
}