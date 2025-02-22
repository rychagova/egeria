/* SPDX-License-Identifier: Apache 2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.accessservices.communityprofile.converters;


import org.odpi.openmetadata.accessservices.communityprofile.metadataelement.PersonRoleElement;
import org.odpi.openmetadata.accessservices.communityprofile.properties.PersonRoleProperties;
import org.odpi.openmetadata.commonservices.generichandlers.OpenMetadataAPIMapper;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.EntityDetail;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.InstanceProperties;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.InstanceType;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.Relationship;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.typedefs.TypeDefCategory;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.repositoryconnector.OMRSRepositoryHelper;


import java.util.List;

/**
 * PersonRoleConverter generates a PersonRoleProperties bean from an PersonRoleProperties entity and the relationships connected to it.
 */
public class PersonRoleConverter<B> extends CommunityProfileOMASConverter<B>
{
    /**
     * Constructor
     *
     * @param repositoryHelper helper object to parse entity
     * @param serviceName name of this component
     * @param serverName local server name
     */
    public PersonRoleConverter(OMRSRepositoryHelper repositoryHelper,
                               String               serviceName,
                               String               serverName)
    {
        super(repositoryHelper, serviceName, serverName);
    }


    /**
     * Using the supplied instances, return a new instance of the bean.  It is used for beans such as
     * an Annotation or DataField bean which combine knowledge from the entity and its linked relationships.
     *
     * @param beanClass name of the class to create
     * @param primaryEntity entity that is the root of the cluster of entities that make up the
     *                      content of the bean
     * @param relationships relationships linking the entities
     * @param methodName calling method
     * @return bean populated with properties from the instances supplied
     * @throws PropertyServerException there is a problem instantiating the bean
     */
    public B getNewComplexBean(Class<B>           beanClass,
                               EntityDetail       primaryEntity,
                               List<Relationship> relationships,
                               String             methodName) throws PropertyServerException
    {
        try
        {
            /*
             * This is initial confirmation that the generic converter has been initialized with an appropriate bean class.
             */
            B returnBean = beanClass.newInstance();

            if (returnBean instanceof PersonRoleElement)
            {
                PersonRoleElement    bean           = (PersonRoleElement) returnBean;
                PersonRoleProperties roleProperties = new PersonRoleProperties();

                if (primaryEntity != null)
                {
                    bean.setElementHeader(this.getMetadataElementHeader(beanClass, primaryEntity, methodName));

                    /*
                     * The initial set of values come from the entity.
                     */
                    InstanceProperties instanceProperties = new InstanceProperties(primaryEntity.getProperties());

                    roleProperties.setRoleId(this.removeQualifiedName(instanceProperties));
                    roleProperties.setTitle(this.removeTitle(instanceProperties));
                    roleProperties.setDescription(this.removeDescription(instanceProperties));
                    roleProperties.setScope(this.removeScope(instanceProperties));
                    roleProperties.setHeadCountLimitSet(instanceProperties.getPropertyValue(OpenMetadataAPIMapper.HEAD_COUNT_PROPERTY_NAME) != null);
                    roleProperties.setHeadCount(this.removeHeadCount(instanceProperties));

                    roleProperties.setAdditionalProperties(this.removeAdditionalProperties(instanceProperties));

                    /*
                     * Any remaining properties are returned in the extended properties.  They are
                     * assumed to be defined in a subtype.
                     */
                    roleProperties.setTypeName(bean.getElementHeader().getType().getTypeName());
                    roleProperties.setExtendedProperties(this.getRemainingExtendedProperties(instanceProperties));

                    if (relationships != null)
                    {
                        int  appointeeCount = 0;

                        for (Relationship relationship : relationships)
                        {
                            if (relationship != null)
                            {
                                InstanceType instanceType = relationship.getType();

                                if (instanceType != null)
                                {
                                    if (repositoryHelper.isTypeOf(serviceName,
                                                                  instanceType.getTypeDefName(),
                                                                  OpenMetadataAPIMapper.PERSON_ROLE_APPOINTMENT_RELATIONSHIP_TYPE_NAME))
                                    {
                                        appointeeCount ++;
                                    }
                                }
                            }
                        }

                        roleProperties.setAppointmentCount(appointeeCount);
                    }

                    bean.setProperties(roleProperties);
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
