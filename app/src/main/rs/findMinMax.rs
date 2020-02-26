#pragma version (1)
#pragma rs java_package_name ( fr.ubordeaux.pimp)
#pragma rs_fp_relaxed


#define NB_COLOR_CHANS 3
#define UCHAR_MIN 0
#define UCHAR_MAX 255

// Returns a Short2 containing the value min and max of the bitmap passed in parameter.
typedef uchar2 minMaxArr[NB_COLOR_CHANS];

// If true,returns the min max of the value (max between 3 RGB channels).
// If not, returns mins and maxs of the 3 RGB channels.
bool valueMode;

#pragma rs reduce(findMinMax) \
    initializer(fMMInit) accumulator(fMMAccumulator) \
     combiner(fMMCombiner) outconverter(fMMOutConverter)

static void fMMInit(minMaxArr* accum) {
    for(uchar i = 0; i < NB_COLOR_CHANS; ++i){
        ((*accum)[i]).x = UCHAR_MAX;
        ((*accum)[i]).y = UCHAR_MIN;
        if(valueMode){
            break;
        }
    }
}

static void fMMAccumulator(minMaxArr* accum, uchar4 in){
    if(valueMode){
        uchar value = max(max(in.r,in.g),in.b);
        (*accum)[0].x = value <= (*accum)[0].x ? value : (*accum)[0].x;
        (*accum)[0].y = value >= (*accum)[0].y ? value : (*accum)[0].y;
        return;
    }

    (*accum)[0].x = in.r <= (*accum)[0].x ? in.r : (*accum)[0].x;
    (*accum)[0].y = in.r >= (*accum)[0].y ? in.r : (*accum)[0].y;
    (*accum)[1].x = in.g <= (*accum)[1].x ? in.g : (*accum)[1].x;
    (*accum)[1].y = in.g >= (*accum)[1].y ? in.g : (*accum)[1].y;
    (*accum)[2].x = in.b <= (*accum)[2].x ? in.b : (*accum)[2].x;
    (*accum)[2].y = in.b >= (*accum)[2].y ? in.b : (*accum)[2].y;
}

static void fMMCombiner(minMaxArr* accum, const minMaxArr* addend){
    for(uchar i = 0; i < NB_COLOR_CHANS; ++i){
        if ((((*accum)[i]).x < 0) || (((*addend)[i]).x < ((*accum)[i]).x))
            (*accum)[i].x = (*addend)[i].x;
        if ((((*accum)[i]).y < 0) || (((*addend)[i]).y > ((*accum)[i]).y))
            (*accum)[i].y = (*addend)[i].y;
        if(valueMode){
            break;
        }
    }
}

static void fMMOutConverter(minMaxArr * result, const minMaxArr * accum){
      for(uchar i = 0; i < NB_COLOR_CHANS; ++i){
        (*result)[i].x = (*accum)[i].x;
        (*result)[i].y = (*accum)[i].y;
        if (valueMode){
            break;
        }
      }
}

