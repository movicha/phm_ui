/*
 * File: app/view/MyContainer.js
 *
 * This file was generated by Sencha Architect version 2.1.0.
 * http://www.sencha.com/products/architect/
 *
 * This file requires use of the Sencha Touch 2.1.x library, under independent license.
 * License of Sencha Architect does not include license for Sencha Touch 2.1.x. For more
 * details see http://www.sencha.com/license or contact license@sencha.com.
 *
 * This file will be auto-generated each and everytime you save your project.
 *
 * Do NOT hand edit this file.
 */

Ext.define('MyApp.view.MyContainer', {
    extend: 'Ext.Container',

    config: {
        layout: {
            type: 'fit'
        },
        items: [
            {
                xtype: 'panel',
                layout: {
                    type: 'vbox'
                },
                items: [
                    {
                        xtype: 'panel',
                        flex: 1,
                        items: [
                            {
                                xtype: 'component',
                                centered: true,
                                html: '<img src="images/high/logo.png" />'
                            }
                        ]
                    },
                    {
                        xtype: 'panel',
                        flex: 1,
                        style: '',
                        items: [
                            {
                                xtype: 'panel',
                                style: 'background:none;margin-top:10%;',
                                hideOnMaskTap: false,
                                layout: {
                                    type: 'vbox'
                                },
                                modal: false,
                                items: [
                                    {
                                        xtype: 'passwordfield',
                                        flex: 0,
                                        height: '',
                                        minHeight: '',
                                        style: 'margin-bottom:10px;margin-left:auto;margin-right:auto;',
                                        width: 300,
                                        label: 'Password',
                                        labelCls: 'labelbg',
                                        labelWidth: 100,
                                        autoCorrect: false,
                                        readOnly: false
                                    },
                                    {
                                        xtype: 'button',
                                        cls: 'gradient',
                                        left: '',
                                        style: 'margin-left:auto;margin-right:auto;',
                                        styleHtmlContent: true,
                                        width: 80,
                                        text: 'Login'
                                    }
                                ]
                            }
                        ]
                    }
                ]
            }
        ]
    }

});