# -*- coding: utf-8 -*-

# Form implementation generated from reading ui file 'mainwindow.ui'
#
# Created by: PyQt5 UI code generator 5.7
#
# WARNING! All changes made in this file will be lost!

from PyQt5 import QtCore, QtGui, QtWidgets

class Ui_Form(object):
    def setupUi(self, Form):
        Form.setObjectName("Form")
        Form.resize(280, 150)
        sizePolicy = QtWidgets.QSizePolicy(QtWidgets.QSizePolicy.Preferred, QtWidgets.QSizePolicy.Preferred)
        sizePolicy.setHorizontalStretch(0)
        sizePolicy.setVerticalStretch(0)
        sizePolicy.setHeightForWidth(Form.sizePolicy().hasHeightForWidth())
        Form.setSizePolicy(sizePolicy)
        self.gridLayout = QtWidgets.QGridLayout(Form)
        self.gridLayout.setSizeConstraint(QtWidgets.QLayout.SetMinimumSize)
        self.gridLayout.setObjectName("gridLayout")
        self.connect_btn = QtWidgets.QPushButton(Form)
        self.connect_btn.setObjectName("connect_btn")
        self.gridLayout.addWidget(self.connect_btn, 3, 1, 1, 1)
        self.port_number_tb = QtWidgets.QLineEdit(Form)
        self.port_number_tb.setObjectName("port_number_tb")
        self.gridLayout.addWidget(self.port_number_tb, 1, 2, 1, 1)
        self.label_3 = QtWidgets.QLabel(Form)
        self.label_3.setObjectName("label_3")
        self.gridLayout.addWidget(self.label_3, 2, 1, 1, 1)
        self.label = QtWidgets.QLabel(Form)
        self.label.setAlignment(QtCore.Qt.AlignCenter)
        self.label.setObjectName("label")
        self.gridLayout.addWidget(self.label, 0, 1, 1, 1)
        self.disconnect_btn = QtWidgets.QPushButton(Form)
        self.disconnect_btn.setObjectName("disconnect_btn")
        self.gridLayout.addWidget(self.disconnect_btn, 3, 2, 1, 1)
        self.host_number_tb = QtWidgets.QLineEdit(Form)
        self.host_number_tb.setObjectName("host_number_tb")
        self.gridLayout.addWidget(self.host_number_tb, 2, 2, 1, 1)
        self.label_2 = QtWidgets.QLabel(Form)
        self.label_2.setObjectName("label_2")
        self.gridLayout.addWidget(self.label_2, 1, 1, 1, 1)
        self.info_label = QtWidgets.QLabel(Form)
        self.info_label.setText("")
        self.info_label.setObjectName("info_label")
        self.gridLayout.addWidget(self.info_label, 0, 2, 1, 1)

        self.retranslateUi(Form)
        QtCore.QMetaObject.connectSlotsByName(Form)

    def retranslateUi(self, Form):
        _translate = QtCore.QCoreApplication.translate
        Form.setWindowTitle(_translate("Form", "Сервер"))
        self.connect_btn.setText(_translate("Form", "Connect"))
        self.port_number_tb.setText(_translate("Form", "9000"))
        self.label_3.setText(_translate("Form", "HOST"))
        self.label.setText(_translate("Form", "Виртуальная мышь"))
        self.disconnect_btn.setText(_translate("Form", "Disconnect"))
        self.host_number_tb.setText(_translate("Form", "localhost"))
        self.label_2.setText(_translate("Form", "PORT"))

