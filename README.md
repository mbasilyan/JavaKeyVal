JavaKeyVal
==========
This project is an experiment done for the purposes of reviewing and playing with:
* Multithreaded server / client designs. 
* The Java language + javadocs.
* Bloom filters

It's also an interesting exercise in building a distributed in memory Key-Value store. 

It has a manager server that is aware of all the machines in the network. 
When the manager recieves a request to add a key/val to the store, it does a few things: 
* Check that the key exists anywhere in the distributed network by checking a Bloom Filter. 
* If the Bloom Filter says no, we just return nothing and save the look up.
* If the Bloom Filter says yes (could still be a false positive), we determine which machine is going to have the key (by hashing the key) and ping that machine to get the value. 

Adding a key/value pair is straight forward: 
* Add the key to the Bloom Filter. 
* Determine which machine should store the key by hashing it and send it to that machine for storage. 

Coolest feature includes using a Bloom Filter to prevent unnecessary look ups. 
(Read more about Bloom Filters here: http://en.wikipedia.org/wiki/Bloom_filter)

Right now the architecture has a few of short comings that might be improved in the future:
* No redundancy
* The central manager is a central/single point of failure

This code isn't really meant for production use. More of a quick & dirty educational exercise. 
