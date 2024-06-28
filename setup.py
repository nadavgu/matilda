from setuptools import setup, find_packages

setup(name='matilda',
      version='0.2.0',
      author='Nadav Gur',
      author_email='nadavgu@gmail.com',
      packages=find_packages(include='matilda*'),
      install_requires=[
            'maddie @ git+git://github.com/nadavgu/maddie-python.git@dev',
            'protobuf~=4.23'
      ]
      )
