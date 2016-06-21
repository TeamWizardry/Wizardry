
uniform int time;

int randID = 0;
vec2 getUV()
{
    vec2 uv = vec2(gl_TexCoord[0]);
    if(uv.x > 1.) {
        randID = int(uv.x);
        uv.x = fract(uv.x);
    }
    uv -= vec2(0.5);
    return uv;
}

float getMillis()
{
	return float(time);
}

vec4 getGlColor()
{
	return vec4(1, 0.5, 0, 1);
}

// uniform int count; // number of ray sets
// uniform float rotationMultiplier; // Ray sets will rotate from -rotSpeed to +rotSpeed degrees per second
//
// uniform vec4 glowColor; // Color of the glow effect applied to the center Default: vec4(1, 1, 1, 1)
// uniform vec4 centerColor; // Color of the blue orb in the center Default: vec4(0, 1, 1, 1)
//
// uniform int rayFade; // Used to control how the side edges of the rays fade. Default: 5
// uniform float glowFade; // Used to control how the centeral glow fades.
// uniform float lengthVariation; // Used to control the range of the ray length RNG. 0.5 means from .5 to 1 in diameter. Default: 0.4
// uniform float centerRadius; // Used to control the radius of the central orb. Default: 0.05

// platform independant

int COUNT = 1; // number of ray sets
float rotationMultiplier = 3.; // the speed that rays will rotate at
float lengthVariation = 0.3; // the amount that the rays will differ in length
int rayFade = 2; // the dropoff of the rays
float centerRadius = 0.05; // the radius of the center orb
vec4 glowColor = vec4(1, 1, 1, 1); // the color of the glow on the inside
vec4 centerColor = vec4(0, 1, 1, 1); // the color of the center orb

// shader

float mod289(float x){return x - floor(x * (1.0 / 289.0)) * 289.0;}
vec4 mod289(vec4 x){return x - floor(x * (1.0 / 289.0)) * 289.0;}
vec4 perm(vec4 x){return mod289(((x * 34.0) + 1.0) * x);}

float noise(vec3 p){
    vec3 a = floor(p);
    vec3 d = p - a;
    d = d * d * (3.0 - 2.0 * d);

    vec4 b = a.xxyy + vec4(0.0, 1.0, 0.0, 1.0);
    vec4 k1 = perm(b.xyxy);
    vec4 k2 = perm(k1.xyxy + b.zzww);

    vec4 c = k2 + a.zzzz;
    vec4 k3 = perm(c);
    vec4 k4 = perm(c + 1.0);

    vec4 o1 = fract(k3 * (1.0 / 41.0));
    vec4 o2 = fract(k4 * (1.0 / 41.0));

    vec4 o3 = o2 * d.z + o1 * (1.0 - d.z);
    vec2 o4 = o3.yw * d.x + o3.xz * (1.0 - d.x);

    return o4.y * d.y + o4.x * (1.0 - d.y);
}

float rand(int n){
	vec3 v = vec3(n, n*9, n*5);
	
	return noise(v);
}

float rand(float n){
	vec3 v = vec3(n, n*9., n*5.);
	
	return noise(v);
}

vec4 sparkle(vec2 uv){
    vec4 color = getGlColor();
	color.w = 0.;
    
    float len = sqrt( uv.x * uv.x + uv.y * uv.y );
    vec2 check = vec2(0, 1);
    
    float dotP = check.x*uv.x + check.y*uv.y;
	float detP = check.x*uv.y - check.y*uv.x;
    
    float angle = degrees(atan(detP, dotP));
    angle += 180.;
    randID += 4;
    for(int i = 1; i <= COUNT; i++) {
        float startAngle = 360. * rand(randID++);
        float angleWidth = 360. / float( int( 3. + rand(randID++) * 5. )*2);
        float speed = rotationMultiplier * ( rand(randID++) - 0.5 );
        
        startAngle += speed * getMillis();
        
        float checkAngle = angle - startAngle;
        
        checkAngle = mod(checkAngle, angleWidth*2.);
        float anglePercent = (checkAngle/angleWidth - 0.5)*2.;
        
        float dist = 0.6-lengthVariation
            +( lengthVariation * rand(randID++)    );
        float distPercent = len/dist;
        
        float w = getGlColor().w
            *( 1.-( distPercent+0.1*rand(checkAngle))    /**/)
            *( 1.-clamp(pow(anglePercent, float(rayFade*2)), 0., 1.)    /**/)
        ;
	    
	    color.w = clamp(color.w + w, 0., 1.);
    }
	
	float centerW = clamp(10.*centerRadius-len*10., 0., 1.);
        
        float foreW = glowColor.w
            *clamp(1.-len*2., 0., 1.)
        ;
        
	float w = color.w;
	color = mix(color, glowColor, foreW);
	color = mix(color, centerColor, centerW);
	color.w = max(w, centerW);
    
    return color;
}

void main( void ) {

	vec2 uv = getUV();

    vec2 check = vec2(0, 1);
    
    float dotP = check.x*uv.x + check.y*uv.y;
	float detP = check.x*uv.y - check.y*uv.x;
    
    float angle = degrees(atan(detP, dotP));
    angle += 180.;

	vec4 col = sparkle(uv);
	gl_FragColor = mix(vec4(0, angle/(360.*2.), 0, 1), col, col.w);
}