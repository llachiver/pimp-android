#pragma version (1)
#pragma rs java_package_name ( fr.ubordeaux.pimp)
#pragma rs_fp_relaxed

#define UCHAR_MIN 0
#define UCHAR_MAX 255

#pragma rs reduce(findMinMax) \
    initializer(fMMInit) accumulator(fMMAccumulator) \
    combiner(fMMCombine)

//Returns a 2-sized array corresponding to the min and max of the image (in char).
static void fMMInit(char2 *minmax) { minmax->x = 'a';
                                     minmax->y = 'b'; }


static void fMMAccumulator(char2* minmax, uchar4 in){
    char value = max(max(in.r,in.g),in.b);
    minmax->x = value < minmax->x ? value : minmax->x;
    minmax->y = value > minmax->y ? value : minmax->y;
}

static void fMMCombine(char2* minmax, const char2* result){
    *minmax = *result;
}