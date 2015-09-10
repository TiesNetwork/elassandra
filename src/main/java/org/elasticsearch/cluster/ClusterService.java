/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.cluster;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.elasticsearch.ElasticsearchIllegalStateException;
import org.elasticsearch.cluster.block.ClusterBlock;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.cluster.routing.ShardRoutingState;
import org.elasticsearch.cluster.routing.operation.OperationRouting;
import org.elasticsearch.cluster.service.PendingClusterTask;
import org.elasticsearch.common.Priority;
import org.elasticsearch.common.component.LifecycleComponent;
import org.elasticsearch.common.unit.TimeValue;

/**
 * The cluster service allowing to both register for cluster state events ({@link ClusterStateListener})
 * and submit state update tasks ({@link ClusterStateUpdateTask}.
 */
public interface ClusterService extends LifecycleComponent<ClusterService> {

    /**
     * The local node.
     */
    DiscoveryNode localNode();

    /**
     * The current state.
     */
    ClusterState state();

    ClusterState state(ClusterState newState);
    
    /**
     * Adds an initial block to be set on the first cluster state created.
     */
    void addInitialStateBlock(ClusterBlock block) throws ElasticsearchIllegalStateException;

    /**
     * Remove an initial block to be set on the first cluster state created.
     */
    void removeInitialStateBlock(ClusterBlock block) throws ElasticsearchIllegalStateException;

    /**
     * The operation routing.
     */
    OperationRouting operationRouting();

    /**
     * Adds a priority listener for updated cluster states.
     */
    void addFirst(ClusterStateListener listener);

    /**
     * Adds last listener.
     */
    void addLast(ClusterStateListener listener);

    /**
     * Adds a listener for updated cluster states.
     */
    void add(ClusterStateListener listener);

    /**
     * Removes a listener for updated cluster states.
     */
    void remove(ClusterStateListener listener);

    /**
     * Add a listener for on/off local node master events
     */
    void add(LocalNodeMasterListener listener);

    /**
     * Remove the given listener for on/off local master events
     */
    void remove(LocalNodeMasterListener listener);

    /**
     * Adds a cluster state listener that will timeout after the provided timeout.
     */
    void add(TimeValue timeout, TimeoutClusterStateListener listener);

    /**
     * Submits a task that will update the cluster state.
     */
    void submitStateUpdateTask(final String source, Priority priority, final ClusterStateUpdateTask updateTask);

    /**
     * Submits a task that will update the cluster state (the task has a default priority of {@link Priority#NORMAL}).
     */
    void submitStateUpdateTask(final String source, final ClusterStateUpdateTask updateTask);

    /**
     * Returns the tasks that are pending.
     */
    List<PendingClusterTask> pendingTasks();

    /**
     * Returns the number of currently pending tasks.
     */
    int numberOfPendingTasks();
    
    
    /**
     * Block until all local shards are STARTED.
     * @param startingLatch
     */
    public void waitShardsStarted();
    
    /**
     * Get indices shard state from gossip endpoints state map.
     * @param address
     * @param index
     * @return
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public ShardRoutingState readIndexShardState(InetAddress address, String index, ShardRoutingState defaultState);
    
    /**
     * Set index shard state in the gossip endpoint map (must be synchronized).
     * @param index
     * @param shardRoutingState
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    public void writeIndexShardSate(String index, ShardRoutingState shardRoutingState) throws JsonGenerationException, JsonMappingException, IOException;
}