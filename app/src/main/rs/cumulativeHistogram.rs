#pragma version (1)
#pragma rs java_package_name ( fr.ubordeaux.pimp)
#pragma rs_fp_relaxed
#include "utils.rs"
#define H_SIZE 256

//Histogram type declaration
typedef uint64_t Histogram[H_SIZE];

//Those params are mainly used for CLAHE
bool clip;
float slope;
//Number of bins in the image
uint32_t nbrBins;

static void clipHistogram(float clipLimit, uint64_t *  histo){
    long clipLimitInt = clipLimit * (nbrBins) / (H_SIZE - 1); //Normalize cliplimit between 0 and 255
    long clipCount = 0;
    for (int i = 0; i < H_SIZE; i++){
        if(histo[i] > clipLimitInt){
            clipCount += histo[i] - clipLimitInt; //Count number of pixels which exceed the clip limit
            histo[i] = clipLimitInt; //Clip
        }
    }

    long redistBatch = clipCount / H_SIZE; //Redistribution pixels average
    long residual = clipCount - redistBatch * H_SIZE; //Residual pixels to increase value

    for(int i = 0; i < H_SIZE; i++)
        histo[i] += redistBatch;
    for(int i = 0; i < residual; i++)
        histo[i]++;
}

//Accumulator declaration
#pragma rs reduce(histogram) \
    accumulator(histAccum) combiner(histCombine)


//LUT table
typedef uchar LUTret[H_SIZE];

static void histAccum(Histogram *h, uchar4 in) {
    uchar value;
    value = max(max(in.r,in.g),in.b);
    ++(*h)[value];
}

static void histCombine(Histogram *accum,
                       const Histogram *addend) {
  for (int i = 0; i < H_SIZE; ++i)
    (*accum)[i] += (*addend)[i];
}

//Reduction kernel to compute the LUT extracted from the cumulative histogram
#pragma rs reduce(LUTCumulativeHistogram) \
    accumulator(histAccum) combiner(histCombine) \
    outconverter(modeOutConvert)

//We compute the cumulative histogram and returns the associated LUT.
static void modeOutConvert(LUTret *result, const Histogram *h) {
    uint64_t acc = 0;
    if (clip)
        clipHistogram(slope, h);
    for (int i = 0; i < H_SIZE; i++) {
        acc += (*h)[i];
        (*result)[i] = (int) ((acc * (H_SIZE-1)) / nbrBins);
    }
}
