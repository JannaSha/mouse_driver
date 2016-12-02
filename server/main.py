from gui import Ui_Form
from PyQt5.QtWidgets import QApplication, QWidget

import sys
import server
import threading


class MainWindow(QWidget):
    def __init__(self, parent=None):
        super(MainWindow, self).__init__(parent)
        self._ui = Ui_Form()
        self._ui.setupUi(self)
        self._is_connect = False
        self.t_stop = threading.Event()

    def on_connect_btn_clicked(self):
        try:
            host = str(self._ui.host_number_tb.text())
            port = int(self._ui.port_number_tb.text())
        except ValueError:
            self._ui.info_label.setText("Некорректные данные!")

        server_run = threading.Thread(
            target=server.start_server, args=(host, port, self.t_stop)
            )
        error = server_run.start()
        if error == -1:
            self._ui.info_label.setText("Ошибка модуля ядра")
        if error == -2:
            self._ui.info_label.setText("Ошибка подключения сокетов")
        self._ui.host_number_tb.setDisabled(True)
        self._ui.port_number_tb.setDisabled(True)
        self._ui.connect_btn.setDisabled(True)
        self._ui.disconnect_btn.setDisabled(False)
        self._is_connect = True

    def on_disconnect_btn_clicked(self):
        if self._is_connect:
            self.t_stop.set()
            self._ui.host_number_tb.setDisabled(False)
            self._ui.port_number_tb.setDisabled(False)
            self._ui.connect_btn.setDisabled(False)
            self._ui.disconnect_btn.setDisabled(True)


def main():
    app = QApplication(sys.argv)
    window = MainWindow()
    window.show()
    return app.exec()


if __name__ == '__main__':
    sys.exit(main())
