import subprocess

from matilda.matilda_runner import MatildaRunner
from matilda.resources.resources import get_resource_path


class JavaProcessMatildaRunner(MatildaRunner):
    def run(self):
        subprocess.Popen(args=["java", "-cp", get_resource_path("agent.jar"), "org.matilda.Main"],
                         stdout=subprocess.PIPE, stderr=subprocess.PIPE)
