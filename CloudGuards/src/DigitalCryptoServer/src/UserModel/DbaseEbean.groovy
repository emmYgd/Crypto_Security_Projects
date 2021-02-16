package UserModel

import groovyx.gpars.dataflow.Promise

import static groovyx.gpars.dataflow.Dataflow.task

class DbaseEbean {

    //Use Ebean ORM layer to authenticate the user
    Promise DbaseCreate = task{}
    Promise DbaseRead = task{}
    Promise DbaseUpdate = task{}
    Promise DbaseDelete = task{}
}
