#pragma version (1)
#pragma rs java_package_name ( fr.ubordeaux.pimp)

//Accumulator declaration
#pragma rs reduce(histogram) \
    accumulator(histAccum) combiner(histCombine)

//Histogram size
#define H_SIZE 256

//Histogram type declaration and size
typedef uint64_t Histogram[H_SIZE];
uint32_t size;

//LUT table
typedef uchar LUTret[H_SIZE];

//Cumulate bins with mode
static void histAccum(Histogram *h, uchar4 in) {
    uchar value;
    value = max(max(in.r,in.g),in.b);
    ++(*h)[value];
}

//Combine fonction
static void histCombine(Histogram *accum,
                       const Histogram *addend) {
  for (int i = 0; i < H_SIZE; ++i)
    (*accum)[i] += (*addend)[i];
}

//Reduction kernel to compute LUTCumulated
#pragma rs reduce(LUTCumulatedHistogram) \
    accumulator(histAccum) combiner(histCombine) \
    outconverter(modeOutConvert)

//Compute cumulated histogram
static void modeOutConvert(LUTret *result, const Histogram *h) {
    uint64_t acc = 0;
    uint64_t hValue;
    for (int i = 0; i < H_SIZE; i++) {
        acc += (*h)[i]; //Cumulative histogram
        (*result)[i] = (int) ((acc * (H_SIZE-1)) / size); //Histogram Equalization formula
    }
}
