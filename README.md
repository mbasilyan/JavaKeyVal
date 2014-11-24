JavaKeyVal
==========
This project is an experiment done for the purposes of reviewing and playing with:
* Multithreaded server / client designs. 
* The Java language + javadocs.
* Bloomfilters

It's also an interesting exercise in building a distributed in memory Key-Value store. 

It has a manager server that is aware of all the machines in the network. 
When the manager recieves a request to add a key/val to the store, it does two things: 
* Check that the key exists anywhere in the distributed network by checking a bloomfilter. 
* If the bloomfilter says no, we just return nothing. 
* If the bloomfilter says yes (could still be a false positive), we determine which machine is going to have the key (by hashing the key) and 
ping that machine and return the value. 

Adding a key/value pair is straight forward: 
* Add the key to the bloom filter. 
* Determine which machine should store the key/val and send it to that machine for storage. 

Coolest feature includes using a bloomfilter to prevent unnecessary look ups. 
Read more about bloomfilters here: http://en.wikipedia.org/wiki/Bloom_filter

Right now the architecture has a bunch of shortcomings:
* No redundancy
* The central manager is a central/single point of failure
