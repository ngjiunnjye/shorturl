package com.github.ngjiunnjye.shorturl.creator.actor

import akka.actor.ExtensionIdProvider
import akka.actor.ExtensionId
import akka.actor.ExtendedActorSystem
import akka.actor.Extension
import akka.cluster.singleton.ClusterSingletonProxy
import akka.cluster.singleton.ClusterSingletonProxySettings

object InventoryManagerProxy extends ExtensionId[CreateRequestProxyImpl] with ExtensionIdProvider {
  override def lookup = InventoryManagerProxy

  override def createExtension(system: ExtendedActorSystem) = {
    new CreateRequestProxyImpl(system)
  }
}

class CreateRequestProxyImpl(system: ExtendedActorSystem) extends Extension {
  val proxy = system.actorOf(ClusterSingletonProxy.props(
      singletonManagerPath = s"/user/InventoryManagerSingleton",
      settings = ClusterSingletonProxySettings(system).withRole(None)),
      name = "InventoryManagerSingleton-proxy")
}
