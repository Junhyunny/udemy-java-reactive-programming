
## Chapter 103. Introduction

* Backpressure / Overflow Strategy
    * buffer - keep in memory
    * drop - Once the queue is full, new items will be dropped
    * latest - Once the queue is full, keep 1 lastest item as and when it arrvies and drop old
    * error - throw error the downstream and cancel pipeline