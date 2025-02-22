/* SPDX-License-Identifier: Apache 2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.accessservices.communityprofile.converters;


import org.odpi.openmetadata.accessservices.communityprofile.metadataelement.ExternalReferenceElement;
import org.odpi.openmetadata.accessservices.communityprofile.properties.ExternalReferenceProperties;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.EntityDetail;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.InstanceProperties;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.Relationship;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.typedefs.TypeDefCategory;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.repositoryconnector.OMRSRepositoryHelper;


/**
 * ExternalReferenceConverter generates an ExternalReferenceProperties bean from an ExternalReferenceProperties entity
 * and a Relationship to it.
 */
public class ExternalReferenceConverter<B> extends CommunityProfileOMASConverter<B>
{
    /**
     * Constructor
     *
     * @param repositoryHelper helper object to parse entity
     * @param serviceName name of this component
     * @param serverName local server name
     */
    public ExternalReferenceConverter(OMRSRepositoryHelper repositoryHelper,
                                      String               serviceName,
                                      String               serverName)
    {
        super(repositoryHelper, serviceName, serverName);
    }


    /**
     * Using the supplied instances, return a new instance of the bean. This is used for beans that have
     * contain a combination of the properties from an entity and a that os a connected relationship.
     *
     * @param beanClass name of the class to create
     * @param entity entity containing the properties
     * @param methodName calling method
     * @return bean populated with properties from the instances supplied
     * @throws PropertyServerException there is a problem instantiating the bean
     */
    @Override
    public B getNewBean(Class<B>     beanClass,
                        EntityDetail entity,
                        String       methodName) throws PropertyServerException
    {
        return getNewBean(beanClass, entity, null, methodName);
    }


    /**
     * Using the supplied instances, return a new instance of the bean. This is used for beans that have
     * contain a combination of the properties from an entity and a that os a connected relationship.
     *
     * @param beanClass name of the class to create
     * @param entity entity containing the properties
     * @param relationship relationship containing the properties
     * @param methodName calling method
     * @return bean populated with properties from the instances supplied
     * @throws PropertyServerException there is a problem instantiating the bean
     */
    @Override
    public B getNewBean(Class<B>     beanClass,
                        EntityDetail entity,
                        Relationship relationship,
                        String       methodName) throws PropertyServerException
    {
        try
        {
            /*
             * This is initial confirmation that the generic converter has been initialized with an appropriate bean class.
             */
            B returnBean = beanClass.newInstance();

            if (returnBean instanceof ExternalReferenceElement)
            {
                ExternalReferenceElement bean = (ExternalReferenceElement) returnBean;

                if (entity != null)
                {
                    bean.setElementHeader(this.getMetadataElementHeader(beanClass, entity, methodName));
                    ExternalReferenceProperties externalReferenceProperties = new ExternalReferenceProperties();

                    /*
                     * The initial set of values come from the entity.
                     */
                    InstanceProperties instanceProperties = new InstanceProperties(entity.getProperties());

                    externalReferenceProperties.setResourceId(this.removeQualifiedName(instanceProperties));
                    externalReferenceProperties.setAdditionalProperties(this.removeAdditionalProperties(instanceProperties));
                    externalReferenceProperties.setResourceDisplayName(this.removeDisplayName(instanceProperties));
                    externalReferenceProperties.setResourceURL(this.removeURL(instanceProperties));
                    externalReferenceProperties.setResourceVersion(this.removeReferenceVersion(instanceProperties));
                    externalReferenceProperties.setResourceDescription(this.removeDescription(instanceProperties));
                    externalReferenceProperties.setOwningOrganization(this.removeOrganization(instanceProperties));

                    /*
                     * Any remaining properties are returned in the extended properties.  They are
                     * assumed to be defined in a subtype.
                     */
                    externalReferenceProperties.setTypeName(bean.getElementHeader().getType().getTypeName());
                    externalReferenceProperties.setExtendedProperties(this.getRemainingExtendedProperties(instanceProperties));

                    if (relationship != null)
                    {
                        instanceProperties = relationship.getProperties();

                        externalReferenceProperties.setLinkDescription(this.getDescription(instanceProperties));
                        externalReferenceProperties.setLinkId(this.getReferenceId(instanceProperties));
                    }

                    bean.setProperties(externalReferenceProperties);
                }
                else
                {
                    handleMissingMetadataInstance(beanClass.getName(), TypeDefCategory.ENTITY_DEF, methodName);
                }
            }

            return returnBean;
        }
        catch (IllegalAccessException | InstantiationException | ClassCastException error)
        {
            super.handleInvalidBeanClass(beanClass.getName(), error, methodName);
        }

        return null;
    }
}
