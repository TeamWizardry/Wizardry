const float PI = 3.14159;
int randID = 0;

uniform float time;

uniform int fanCount;
uniform float fanSpeedMax;
uniform float fanSpeedMin;
uniform float fanSizeMin;
uniform float fanSizeMax;
uniform float fanJitterMin;
uniform float fanJitterMax;
uniform int fanBladesMin;
uniform int fanBladesMax;


// the current time in seconds
float timeSec() {
    return time;
}

vec4 glColor() {
    return gl_Color;
}

// ======== begin platform-independant code

// gets the angle given a uv position
float getUVangle(vec2 uv) {
    return atan(uv.x, -uv.y) + PI;
    // +PI because the result is from -pi to +pi. I want it from 0 to 2*pi
}

// rand
float rand(vec2 n) { 
    return fract(sin(dot(n, vec2(12.9898, 4.1414))) * 43758.5453);
}

// noise
float noise(vec2 p){
    vec2 ip = floor(p);
    vec2 u = fract(p);
    u = u*u*(3.0-2.0*u);

    float res = mix(
        mix(rand(ip),rand(ip+vec2(1.0,0.0)),u.x),
        mix(rand(ip+vec2(0.0,1.0)),rand(ip+vec2(1.0,1.0)),u.x),u.y);
    return res*res;
}

// interpolates between two values using the control input
float range(float low, float high, float control) {
    return low + control * (high-low);
}

// gets a random number for a fan
float fanRand(int fan, int offset, float param) {
    return rand(vec2(float(fan), float(offset + 100*randID) + param));
}

// gets the angle offset of the fan
float getFanRotation(int fanID) {
    float speed  = range(fanSpeedMin*2.*PI, fanSpeedMax*2.*PI, fanRand(fanID, 10, 0.));
    float initial = range(0., 2.*PI, fanRand(fanID, 11, 0.));
    
    return initial + speed * timeSec();
}

int getBladeCount(int fanID) {
    return int(range(float(fanBladesMin), float(fanBladesMax), fanRand(fanID, 30, 0.)));
}

// gets the length of the fan given an angle and an index
float getFanLength(int fanID, float angle) {
    float bladeSweep = (2.*PI)/float(getBladeCount(fanID));
    int bladeNum = int(angle/bladeSweep);
    float main = range(fanSizeMin, fanSizeMax, fanRand(fanID, 20, float(bladeNum)));
    
    float jitter = range(fanJitterMin, fanJitterMax,
                         rand(vec2( float(int(degrees(angle))  ), 0 ))
                        );
    return main + jitter;
}

// -2 to 2 along the blade area. -1 to 1 is the area with the blade
float getFanBladeCurveCoord(int fanID, float angle) {
    float bladeSweep = (2.*PI)/float(getBladeCount(fanID)*2);
    float bladeAngle = mod(angle, bladeSweep*2.);
    return (( bladeAngle / bladeSweep )-1.)*2.;
}

vec4 particle(vec2 uv) {
    uv = uv - vec2(0.5);
    uv = uv * 2.;
    vec4 color = vec4(0);
    
    float uvRadius = sqrt( uv.x * uv.x + uv.y * uv.y );
    float angle = getUVangle(uv);
    
    for(int i = 0; i < fanCount; i++) {
        // the angle relative to the fan
        float fanAngle = mod( angle + getFanRotation(i), 2.*PI);
        // the length of the fan
        float fanLength = getFanLength(i, fanAngle);
        // the percent of the blade. -1 to 1 is the blade
        // the rest is blank space between the blades
        float fanBladeCurveCoord = getFanBladeCurveCoord(i, fanAngle);
        
        float w = 1.;
        
        // the sideways blade fading
        w *= 1.-clamp(pow(fanBladeCurveCoord, 4.), 0., 1.);
        // the length wise fading
        w *= clamp(pow(1.-clamp(uvRadius/fanLength, 0., 1.), 1./3.), 0., 1.);
        
        // set the alpha
        color.w = max(w, color.w);
    }
    
    float w = color.w;
    
    color = mix(vec4(1), glColor(), clamp(uvRadius*2., 0., 1.));
    
    color.w = w * glColor().w;
    
    return color;
}

// ======== end platform-independant code

void main()
{
    vec2 uv = vec2(gl_TexCoord[0]);
    if(uv.x > 1.) {
        randID = int(uv.x);
        uv.x = fract(uv.x);
    }
    vec4 c = particle(uv);
    gl_FragColor = c;//mix(vec4(0,0,0,1), c, c.w);
}