/* SPDX-License-Identifier: Apache 2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.engineservices.governanceaction.handlers;

import org.odpi.openmetadata.accessservices.governanceengine.client.GovernanceEngineClient;
import org.odpi.openmetadata.accessservices.governanceengine.properties.GovernanceEngineProperties;
import org.odpi.openmetadata.engineservices.governanceaction.context.GovernanceListenerManager;
import org.odpi.openmetadata.engineservices.governanceaction.context.OpenMetadataStoreClient;
import org.odpi.openmetadata.engineservices.governanceaction.ffdc.GovernanceActionErrorCode;
import org.odpi.openmetadata.frameworks.auditlog.AuditLog;
import org.odpi.openmetadata.engineservices.governanceaction.ffdc.GovernanceActionAuditCode;
import org.odpi.openmetadata.frameworks.connectors.Connector;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.governanceaction.*;
import org.odpi.openmetadata.frameworks.governanceaction.properties.ActionTargetElement;
import org.odpi.openmetadata.frameworks.governanceaction.properties.CompletionStatus;
import org.odpi.openmetadata.frameworks.governanceaction.properties.RequestSourceElement;
import org.odpi.openmetadata.governanceservers.enginehostservices.admin.GovernanceServiceHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * GovernanceActionServiceHandler provides the thread to run a governance action service.  A new instance is created for each request.
 */
public class GovernanceActionServiceHandler extends GovernanceServiceHandler
{
    private GovernanceActionService    governanceActionService;
    private String                     governanceActionServiceType;
    private GovernanceContext          governanceContext;


    /**
     * Constructor sets up the key parameters for running the governance action service.
     * This call is made on the REST call's thread so the properties are just cached.
     * The action happens in the run() method.
     *
     * @param governanceActionEngineProperties properties of the governance action engine - used for message logging
     * @param governanceActionEngineGUID unique Identifier of the governance action engine - used for message logging
     * @param governanceActionGUID unique identifier of the governance action that triggered this governance service
     * @param requestType incoming request
     * @param requestParameters parameters associated with the request type
     * @param governanceActionServiceName name of this governance action service - used for message logging
     * @param governanceActionServiceConnector connector that does the work
     * @param auditLog destination for log messages
     */
    GovernanceActionServiceHandler(GovernanceEngineProperties governanceActionEngineProperties,
                                   String                     governanceActionEngineGUID,
                                   String                     userId,
                                   String                     governanceActionGUID,
                                   String                     requestType,
                                   Map<String, String>        requestParameters,
                                   List<RequestSourceElement> requestSourceElements,
                                   List<ActionTargetElement>  actionTargetElements,
                                   String                     governanceActionServiceName,
                                   Connector                  governanceActionServiceConnector,
                                   String                     partnerServerName,
                                   String                     partnerServerPlatformURLRoot,
                                   GovernanceEngineClient     governanceEngineClient,
                                   GovernanceListenerManager  governanceListenerManager,
                                   AuditLog                   auditLog) throws InvalidParameterException
    {
        super(governanceActionEngineProperties,
              governanceActionEngineGUID,
              governanceActionGUID,
              requestType,
              governanceActionServiceName,
              governanceActionServiceConnector,
              auditLog);

        final String actionDescription = "Initializing GovernanceActionService";
        final String governanceActionServiceConnectorParameterName = "governanceActionServiceConnector";

        final String watchdogGovernanceActionServiceType     = "OpenWatchdogService";
        final String provisioningGovernanceActionServiceType = "OpenProvisioningService";
        final String verificationGovernanceActionServiceType = "OpenVerificationService";
        final String triageGovernanceActionServiceType       = "OpenTriageService";
        final String remediationGovernanceActionServiceType  = "OpenRemediationService";

        try
        {
            OpenMetadataClient openMetadataClient = new OpenMetadataStoreClient(partnerServerName,
                                                                                partnerServerPlatformURLRoot,
                                                                                governanceEngineClient,
                                                                                governanceListenerManager,
                                                                                userId);

            if (governanceActionServiceConnector instanceof WatchdogGovernanceActionService)
            {
                WatchdogGovernanceContext context = new WatchdogGovernanceContext(userId,
                                                                                  governanceActionGUID,
                                                                                  requestType,
                                                                                  requestParameters,
                                                                                  requestSourceElements,
                                                                                  actionTargetElements,
                                                                                  openMetadataClient);

                WatchdogGovernanceActionService service = (WatchdogGovernanceActionService)governanceActionServiceConnector;

                service.setGovernanceContext(context);
                service.setAuditLog(auditLog);

                this.governanceContext = context;
                this.governanceActionService = service;
                this.governanceActionServiceType = watchdogGovernanceActionServiceType;
            }
            else if (governanceActionServiceConnector instanceof VerificationGovernanceActionService)
            {
                VerificationGovernanceContext context = new VerificationGovernanceContext(userId,
                                                                                          governanceActionGUID,
                                                                                          requestType,
                                                                                          requestParameters,
                                                                                          requestSourceElements,
                                                                                          actionTargetElements,
                                                                                          openMetadataClient);

                VerificationGovernanceActionService service = (VerificationGovernanceActionService)governanceActionServiceConnector;

                service.setGovernanceContext(context);
                service.setAuditLog(auditLog);

                this.governanceContext = context;
                this.governanceActionService = service;
                this.governanceActionServiceType = verificationGovernanceActionServiceType;
            }
            else if (governanceActionServiceConnector instanceof TriageGovernanceActionService)
            {
                TriageGovernanceContext context = new TriageGovernanceContext(userId,
                                                                              governanceActionGUID,
                                                                              requestType,
                                                                              requestParameters,
                                                                              requestSourceElements,
                                                                              actionTargetElements,
                                                                              openMetadataClient);

                TriageGovernanceActionService service = (TriageGovernanceActionService)governanceActionServiceConnector;

                service.setGovernanceContext(context);
                service.setAuditLog(auditLog);

                this.governanceContext = context;
                this.governanceActionService = service;
                this.governanceActionServiceType = triageGovernanceActionServiceType;
            }
            else if (governanceActionServiceConnector instanceof RemediationGovernanceActionService)
            {
                RemediationGovernanceContext context = new RemediationGovernanceContext(userId,
                                                                                        governanceActionGUID,
                                                                                        requestType,
                                                                                        requestParameters,
                                                                                        requestSourceElements,
                                                                                        actionTargetElements,
                                                                                        openMetadataClient);

                RemediationGovernanceActionService service = (RemediationGovernanceActionService)governanceActionServiceConnector;

                service.setGovernanceContext(context);
                service.setAuditLog(auditLog);

                this.governanceContext = context;
                this.governanceActionService = service;
                this.governanceActionServiceType = remediationGovernanceActionServiceType;
            }
            else if (governanceActionServiceConnector instanceof ProvisioningGovernanceActionService)
            {
                ProvisioningGovernanceContext context = new ProvisioningGovernanceContext(userId,
                                                                                          governanceActionGUID,
                                                                                          requestType,
                                                                                          requestParameters,
                                                                                          requestSourceElements,
                                                                                          actionTargetElements,
                                                                                          openMetadataClient);

                ProvisioningGovernanceActionService service = (ProvisioningGovernanceActionService)governanceActionServiceConnector;

                service.setGovernanceContext(context);
                service.setAuditLog(auditLog);

                this.governanceContext = context;
                this.governanceActionService = service;
                this.governanceActionServiceType = provisioningGovernanceActionServiceType;
            }
            else if (governanceActionServiceConnector instanceof GovernanceActionService)
            {
                auditLog.logMessage(actionDescription,
                                      GovernanceActionAuditCode.UNKNOWN_GOVERNANCE_ACTION_SERVICE.getMessageDefinition(governanceActionServiceName,
                                                                                                                       requestType,
                                                                                                                       governanceActionServiceConnector.getClass().getName()));
                throw new InvalidParameterException(GovernanceActionErrorCode.UNKNOWN_GOVERNANCE_ACTION_SERVICE.getMessageDefinition(governanceActionServiceName,
                                                                                                                                     requestType,
                                                                                                                                     governanceActionServiceConnector.getClass().getName()),
                                                    this.getClass().getName(),
                                                    actionDescription,
                                                    governanceActionServiceConnectorParameterName);
            }
            else
            {
                auditLog.logMessage(actionDescription,
                                      GovernanceActionAuditCode.NOT_GOVERNANCE_ACTION_SERVICE.getMessageDefinition(governanceActionServiceName,
                                                                                                                   requestType,
                                                                                                                   governanceActionServiceConnector.getClass().getName()));
                throw new InvalidParameterException(GovernanceActionErrorCode.NOT_GOVERNANCE_ACTION_SERVICE.getMessageDefinition(governanceActionServiceName,
                                                                                                                                 requestType,
                                                                                                                                 governanceActionServiceConnector.getClass().getName()),
                                                    this.getClass().getName(),
                                                    actionDescription,
                                                    governanceActionServiceConnectorParameterName);
            }
        }
        catch (InvalidParameterException error)
        {
            throw error;
        }
        catch (Exception error)
        {
            auditLog.logException(actionDescription,
                                  GovernanceActionAuditCode.INVALID_GOVERNANCE_ACTION_SERVICE.getMessageDefinition(governanceActionServiceName,
                                                                                                                   requestType,
                                                                                                                   error.getClass().getName(),
                                                                                                                   error.getMessage()),
                                  error);
            throw new InvalidParameterException(GovernanceActionErrorCode.INVALID_GOVERNANCE_ACTION_SERVICE.getMessageDefinition(governanceActionServiceName,
                                                                                                                                 requestType,
                                                                                                                                 error.getClass().getName(),
                                                                                                                                 error.getMessage()),
                                                this.getClass().getName(),
                                                actionDescription,
                                                error,
                                                governanceActionServiceConnectorParameterName);
        }
    }


    /**
     * This is the method that provides the behaviour of the thread.
     */
    @Override
    public void run()
    {
        Date startTime;
        Date endTime;

        final String actionDescription = "Run governance service";

        try
        {
            auditLog.logMessage(actionDescription,
                                GovernanceActionAuditCode.GOVERNANCE_ACTION_SERVICE_STARTING.getMessageDefinition(governanceActionServiceType,
                                                                                                                  governanceActionServiceName,
                                                                                                                  requestType,
                                                                                                                  governanceActionEngineProperties.getQualifiedName(),
                                                                                                                  governanceActionEngineGUID));



            startTime = new Date();
            governanceActionService.start();
            endTime = new Date();

            CompletionStatus completionStatus = governanceContext.getCompletionStatus();

            if (completionStatus == null)
            {
                auditLog.logMessage(actionDescription,
                                    GovernanceActionAuditCode.GOVERNANCE_ACTION_SERVICE_RETURNED.getMessageDefinition(governanceActionServiceType,
                                                                                                                      governanceActionServiceName,
                                                                                                                      requestType,
                                                                                                                      Long.toString(
                                                                                                                              endTime.getTime() - startTime.getTime())));
            }
            else
            {
                auditLog.logMessage(actionDescription,
                                    GovernanceActionAuditCode.GOVERNANCE_ACTION_SERVICE_COMPLETE.getMessageDefinition(governanceActionServiceType,
                                                                                                                      governanceActionServiceName,
                                                                                                                      requestType,
                                                                                                                      Long.toString(endTime.getTime() - startTime.getTime())));
            }

        }
        catch (Throwable  error)
        {
            auditLog.logException(actionDescription,
                                  GovernanceActionAuditCode.GOVERNANCE_ACTION_SERVICE_FAILED.getMessageDefinition(governanceActionServiceType,
                                                                                                                  governanceActionServiceName,
                                                                                                                  error.getClass().getName(),
                                                                                                                  governanceActionEngineProperties.getQualifiedName(),
                                                                                                                  governanceActionEngineGUID,
                                                                                                                  error.getMessage()),
                                  error.toString(),
                                  error);

            try
            {
                CompletionStatus completionStatus = governanceContext.getCompletionStatus();

                if (completionStatus == null)
                {
                    governanceContext.recordCompletionStatus(CompletionStatus.FAILED, null, null);
                }
            }
            catch (Throwable statusError)
            {
                auditLog.logException(actionDescription,
                                      GovernanceActionAuditCode.EXC_ON_ERROR_STATUS_UPDATE.getMessageDefinition(governanceActionEngineProperties.getDisplayName(),
                                                                                                             governanceActionServiceName,
                                                                                                             statusError.getClass().getName(),
                                                                                                             statusError.getMessage()),
                                      statusError.toString(),
                                      statusError);
            }
        }
    }
}
