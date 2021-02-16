package EmmaStreamingMediumScale

//In this solution, received Files broken into Chunks,
// Each chunks are then compressed, encrypted and then stored to a Cache
//Session Requests are then monitored in real time to ensure continuity:
//if the Video Stream session requests are active:
// get each compressed encrypted chunk from the Cache...
// Send each chunks over through the network to the JS local Cache(through AJAX)
// Await further requests from this session...
interface CacheToStreamSolution {

}
