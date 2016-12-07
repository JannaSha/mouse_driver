import sys
import threading

from PyQt5.QtWidgets import QApplication, QWidget
from PyQt5.QtWidgets import QMessageBox

from core import server
from gui.mainwindow import Ui_Form


class MainWindow(QWidget):
    def __init__(self, parent=None):
        super(MainWindow, self).__init__(parent)
        self._ui = Ui_Form()
        self._ui.setupUi(self)
        self._is_connect = False
        self._server_thread = None
        self.__connect_signals()
        self._stop_event = threading.Event()
        self._run_server = None

    def __connect_signals(self):
        self._ui.connect_btn.clicked.connect(
            self.handle_connect_btn)
        self._ui.disconnect_btn.clicked.connect(
            self.handle_disconnect_btn)

    def handle_connect_btn(self):
        if not self._is_connect:
            try:
                host = str(self._ui.host_number_tb.text())
                port = int(self._ui.port_number_tb.text())
            except ValueError:
                self.show_error_message(msg="Incorrect name of port or host")
                return

            self._server_thread = server.ServerWork(host, port)
            self._server_thread.is_run = True
            self._run_server = threading.Thread(
                target=self._server_thread.run, args=(self._stop_event,))
            error = self._run_server.start()
            if error == -2:
                self.show_error_message(msg="Error of kernel module")
                exit(1)
            elif error == -1:
                self.show_error_message(msg="Error of socket connection")
                return
            else:
                self._ui.host_number_tb.setDisabled(True)
                self._ui.port_number_tb.setDisabled(True)
                self._ui.connect_btn.setDisabled(True)
                self._ui.disconnect_btn.setDisabled(False)
                self._is_connect = True
        else:
            print('Server already connected')

    def handle_disconnect_btn(self):
        if self._is_connect:
            self._stop_event.set()
            self._run_server.join()
            self._is_connect = False
            self._ui.host_number_tb.setDisabled(False)
            self._ui.port_number_tb.setDisabled(False)
            self._ui.connect_btn.setDisabled(False)
            self._ui.disconnect_btn.setDisabled(True)

    def closeEvent(self, QCloseEvent):
        if self._is_connect:
            self._stop_event.set()
            self._run_server.join()

        QCloseEvent.accept()

    def show_error_message(self, msg):
        QMessageBox.critical(self, "ERROR", msg, QMessageBox.Ok)

    def show_information_message(self, msg):
        QMessageBox.critical(self, "Information", msg, QMessageBox.Ok)


def main():
    app = QApplication(sys.argv)
    window = MainWindow()
    window.show()
    return app.exec()

if __name__ == '__main__':
    sys.exit(main())
