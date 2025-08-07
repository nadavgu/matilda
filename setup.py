from setuptools import setup, find_packages

setup(name='matilda',
      version='0.9.0',
      author='Nadav Gur',
      author_email='nadavgu@gmail.com',
      packages=find_packages(include='matilda*'),
      package_data={'matilda': ['resources/*']},
      install_requires=[
            'maddie @ git+ssh://git@github.com/nadavgu/maddie-python.git@dev',
            'protobuf~=5.28'
      ]
      )
