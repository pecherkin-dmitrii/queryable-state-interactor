package neoflex

import org.apache.flink.api.common.JobID
import org.apache.flink.api.common.state.{ListStateDescriptor, MapStateDescriptor, ValueStateDescriptor}
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.queryablestate.client.QueryableStateClient

import scala.util.Try

object QuaryableStateInteractor extends App {
  val params = getParams(args)

  val listStateDescriptor = new ListStateDescriptor("listState", createTypeInformation[String])
  val valueStateDescriptor = new ValueStateDescriptor("valueState", createTypeInformation[String])
  val mapStateDescriptor = new MapStateDescriptor("mapState", createTypeInformation[String], createTypeInformation[Int])

  val client = new QueryableStateClient(params.tmHost, params.proxyPort)

  val jobId = JobID.fromHexString(params.jobID)
  val queryableListStateName = params.queryableListStateName
  val queryableValueStateName = params.queryableValueStateName
  val queryableMapStateName = params.queryableMapStateName
  val key = params.key
  val keyType = createTypeInformation[String]

  val listStateByKey = client.getKvState(jobId, queryableListStateName, key, keyType, listStateDescriptor)
  val valueStateByKey = client.getKvState(jobId, queryableValueStateName, key, keyType, valueStateDescriptor)
  val mapStateByKey = client.getKvState(jobId, queryableMapStateName, key, keyType, mapStateDescriptor)

  println(s"States's content by key ${params.key}:")
  println(s"List state:")
  listStateByKey.get.get.forEach(println)

  println(s"Value state:")
  println(valueStateByKey.get.value)

  println(s"Map state:")
  mapStateByKey.get.entries().forEach(entry => println(s"Key=${entry.getKey}, value=${entry.getValue}"))

  client.shutdownAndWait()

  private def getParams(args: Array[String]): StateInteractorParams = {
    val tmHost = Try(args(0)).toOption.getOrElse("127.0.1.1")
    val proxyPort = Try(args(1).toInt).toOption.getOrElse(9069)
    val jobID = Try(args(2)).toOption.getOrElse("35ba6d3d26eac6103338cb2f85f94b3b")
    val queryableListStateName = Try(args(3)).toOption.getOrElse("testQueryableListState")
    val queryableValueStateName = Try(args(4)).toOption.getOrElse("testQueryableValueState")
    val queryableMapStateName = Try(args(5)).toOption.getOrElse("testQueryableMapState")
    val key = Try(args(6)).toOption.getOrElse("16")
    StateInteractorParams(tmHost, proxyPort, jobID, queryableListStateName, queryableValueStateName, queryableMapStateName, key)
  }
}

case class StateInteractorParams(tmHost: String,
                                 proxyPort: Int,
                                 jobID: String,
                                 queryableListStateName: String,
                                 queryableValueStateName: String,
                                 queryableMapStateName: String,
                                 key: String)
