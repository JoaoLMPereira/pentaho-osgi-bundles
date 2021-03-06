/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2014 by Pentaho : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.osgi.notification.webservice;

import org.pentaho.osgi.notification.api.MatchCondition;
import org.pentaho.osgi.notification.api.NotificationObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by bryan on 9/22/14.
 */
public class NotificationRequestMatchCondition implements MatchCondition {
  private final Map<String, Map<String, Long>> relevantMap = new HashMap<String, Map<String, Long>>();

  public Set<String> getTypes() {
    return relevantMap.keySet();
  }

  public NotificationRequestMatchCondition( NotificationRequestWrapper notificationRequestWrapper ) {
    for ( NotificationRequest notificationRequest : notificationRequestWrapper.getRequests() ) {
      String type = notificationRequest.getNotificationType();
      Map<String, Long> typeMap = new HashMap<String, Long>();
      relevantMap.put( type, typeMap );
      for ( NotificationRequestEntry notificationRequestEntry : notificationRequest.getEntries() ) {
        Long sequence = notificationRequestEntry.getSequence();
        if ( sequence == null ) {
          sequence = 0L;
        }
        typeMap.put( notificationRequestEntry.getId(), sequence );
      }
    }
  }

  @Override public boolean matches( Object object ) {
    if ( !( object instanceof NotificationObject ) ) {
      return false;
    }
    NotificationObject notificationObject = (NotificationObject) object;
    String type = notificationObject.getType();
    Map<String, Long> typeMap = relevantMap.get( type );
    if ( typeMap == null ) {
      return false;
    }
    Long sequence = typeMap.get( notificationObject.getId() );
    if ( sequence != null ) {
      return notificationObject.getSequence() >= sequence;
    }
    return false;
  }
}
